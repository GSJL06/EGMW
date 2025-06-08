-- EducaGestor360 Database Initialization Script

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS educagestor360;

-- Use the database
\c educagestor360;

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create sequences
CREATE SEQUENCE IF NOT EXISTS institution_seq START 1;
CREATE SEQUENCE IF NOT EXISTS user_seq START 1;
CREATE SEQUENCE IF NOT EXISTS student_seq START 1;
CREATE SEQUENCE IF NOT EXISTS teacher_seq START 1;
CREATE SEQUENCE IF NOT EXISTS course_seq START 1;
CREATE SEQUENCE IF NOT EXISTS enrollment_seq START 1;
CREATE SEQUENCE IF NOT EXISTS grade_seq START 1;
CREATE SEQUENCE IF NOT EXISTS attendance_seq START 1;
CREATE SEQUENCE IF NOT EXISTS educational_resource_seq START 1;

-- Create tables

-- Institution table
CREATE TABLE IF NOT EXISTS institutions (
    id BIGINT PRIMARY KEY DEFAULT nextval('institution_seq'),
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    phone VARCHAR(20),
    email VARCHAR(255),
    website VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Users table (for authentication)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY DEFAULT nextval('user_seq'),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'TEACHER', 'STUDENT')),
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Students table
CREATE TABLE IF NOT EXISTS students (
    id BIGINT PRIMARY KEY DEFAULT nextval('student_seq'),
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    institution_id BIGINT REFERENCES institutions(id),
    student_code VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(10) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    address VARCHAR(500),
    phone VARCHAR(20),
    emergency_contact VARCHAR(255),
    emergency_phone VARCHAR(20),
    enrollment_date DATE DEFAULT CURRENT_DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'GRADUATED', 'SUSPENDED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Teachers table
CREATE TABLE IF NOT EXISTS teachers (
    id BIGINT PRIMARY KEY DEFAULT nextval('teacher_seq'),
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    institution_id BIGINT REFERENCES institutions(id),
    employee_code VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(10) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    address VARCHAR(500),
    phone VARCHAR(20),
    specialization VARCHAR(255),
    hire_date DATE DEFAULT CURRENT_DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'ON_LEAVE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Courses table
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT PRIMARY KEY DEFAULT nextval('course_seq'),
    institution_id BIGINT REFERENCES institutions(id),
    teacher_id BIGINT REFERENCES teachers(id),
    code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    credits INTEGER DEFAULT 1,
    max_students INTEGER DEFAULT 30,
    schedule VARCHAR(255),
    classroom VARCHAR(100),
    start_date DATE,
    end_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'COMPLETED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Enrollments table (Student-Course relationship)
CREATE TABLE IF NOT EXISTS enrollments (
    id BIGINT PRIMARY KEY DEFAULT nextval('enrollment_seq'),
    student_id BIGINT REFERENCES students(id) ON DELETE CASCADE,
    course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE,
    enrollment_date DATE DEFAULT CURRENT_DATE,
    status VARCHAR(20) DEFAULT 'ENROLLED' CHECK (status IN ('ENROLLED', 'COMPLETED', 'DROPPED', 'FAILED')),
    final_grade DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(student_id, course_id)
);

-- Grades table
CREATE TABLE IF NOT EXISTS grades (
    id BIGINT PRIMARY KEY DEFAULT nextval('grade_seq'),
    enrollment_id BIGINT REFERENCES enrollments(id) ON DELETE CASCADE,
    assignment_name VARCHAR(255) NOT NULL,
    grade DECIMAL(5,2) NOT NULL,
    max_grade DECIMAL(5,2) DEFAULT 100.00,
    weight DECIMAL(5,2) DEFAULT 1.00,
    grade_date DATE DEFAULT CURRENT_DATE,
    comments TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Attendance table
CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT PRIMARY KEY DEFAULT nextval('attendance_seq'),
    enrollment_id BIGINT REFERENCES enrollments(id) ON DELETE CASCADE,
    attendance_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PRESENT', 'ABSENT', 'LATE', 'EXCUSED')),
    comments TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(enrollment_id, attendance_date)
);

-- Educational Resources table
CREATE TABLE IF NOT EXISTS educational_resources (
    id BIGINT PRIMARY KEY DEFAULT nextval('educational_resource_seq'),
    course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE,
    teacher_id BIGINT REFERENCES teachers(id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    resource_type VARCHAR(50) NOT NULL CHECK (resource_type IN ('DOCUMENT', 'VIDEO', 'AUDIO', 'LINK', 'IMAGE', 'OTHER')),
    file_path VARCHAR(500),
    file_size BIGINT,
    mime_type VARCHAR(100),
    is_public BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_students_user_id ON students(user_id);
CREATE INDEX IF NOT EXISTS idx_students_institution_id ON students(institution_id);
CREATE INDEX IF NOT EXISTS idx_students_student_code ON students(student_code);
CREATE INDEX IF NOT EXISTS idx_teachers_user_id ON teachers(user_id);
CREATE INDEX IF NOT EXISTS idx_teachers_institution_id ON teachers(institution_id);
CREATE INDEX IF NOT EXISTS idx_teachers_employee_code ON teachers(employee_code);
CREATE INDEX IF NOT EXISTS idx_courses_institution_id ON courses(institution_id);
CREATE INDEX IF NOT EXISTS idx_courses_teacher_id ON courses(teacher_id);
CREATE INDEX IF NOT EXISTS idx_courses_code ON courses(code);
CREATE INDEX IF NOT EXISTS idx_enrollments_student_id ON enrollments(student_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_course_id ON enrollments(course_id);
CREATE INDEX IF NOT EXISTS idx_grades_enrollment_id ON grades(enrollment_id);
CREATE INDEX IF NOT EXISTS idx_attendance_enrollment_id ON attendance(enrollment_id);
CREATE INDEX IF NOT EXISTS idx_attendance_date ON attendance(attendance_date);
CREATE INDEX IF NOT EXISTS idx_educational_resources_course_id ON educational_resources(course_id);
CREATE INDEX IF NOT EXISTS idx_educational_resources_teacher_id ON educational_resources(teacher_id);

-- Insert sample data
INSERT INTO institutions (name, address, phone, email, website) VALUES
('Instituto Tecnológico Nacional', 'Calle Principal 123, Ciudad', '+57 1 234-5678', 'info@itn.edu.co', 'https://www.itn.edu.co');

-- Insert default admin user
INSERT INTO users (username, email, password, role) VALUES
('admin', 'admin@educagestor.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN');

-- Create triggers for updated_at timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_institutions_updated_at BEFORE UPDATE ON institutions FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_students_updated_at BEFORE UPDATE ON students FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_teachers_updated_at BEFORE UPDATE ON teachers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_courses_updated_at BEFORE UPDATE ON courses FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_enrollments_updated_at BEFORE UPDATE ON enrollments FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_grades_updated_at BEFORE UPDATE ON grades FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_attendance_updated_at BEFORE UPDATE ON attendance FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_educational_resources_updated_at BEFORE UPDATE ON educational_resources FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
