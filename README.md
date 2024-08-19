-- 데이터베이스 생성
CREATE DATABASE console_board;
USE console_board;

-- users 테이블 (회원 관리)
CREATE TABLE users(
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255) NOT NULL,
    gender CHAR(1),                 -- 성별(M/F)
    last_login DATETIME,            -- 마지막 로그인 시간
    last_logout DATETIME,           -- 마지막 로그아웃 시간
    is_deleted BOOLEAN DEFAULT FALSE -- 회원 탈퇴 여부
);

-- users 테이블에 role 컬럼 추가
ALTER TABLE users ADD COLUMN role VARCHAR(50);

-- login_history 테이블 (로그인/로그아웃 기록)
CREATE TABLE login_history(
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50),
    login_time TIMESTAMP,
    logout_time TIMESTAMP,
    FOREIGN KEY (username) REFERENCES users(username)
);

-- deleted_users 테이블 (회원 탈퇴 기록)
CREATE TABLE deleted_users(
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255) NOT NULL,
    gender CHAR(1),        
    delete_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- 회원 탈퇴 시간
);

-- posts 테이블 (게시물 관리)
CREATE TABLE posts (
    post_id INT PRIMARY KEY AUTO_INCREMENT,  -- 게시물 번호 (자동 증가)
    author VARCHAR(50),                      -- 작성자
    title VARCHAR(255),                      -- 제목
    content TEXT,                            -- 내용
    password VARCHAR(100),                   -- 수정/삭제 시 사용할 비밀번호
    view_count INT DEFAULT 0,                -- 읽은수 (조회수)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- 작성일
);

-- 게시물 등록을 위한 저장 프로시저
DELIMITER $$
CREATE PROCEDURE AddPost(
    IN p_author VARCHAR(50),
    IN p_title VARCHAR(255),
    IN p_content TEXT,
    IN p_password VARCHAR(100),
    IN p_created_at TIMESTAMP  -- 추가된 필드: 작성 시간
)
BEGIN
	INSERT INTO posts (author, title, content, password, created_at)
    VALUES (p_author, p_title, p_content, p_password, p_created_at);
END $$
DELIMITER ;

-- posts 테이블에 더미 데이터 삽입
INSERT INTO posts (author, title, content, password, view_count)
VALUES
('Alice', '게시물 제목 1', '게시물 내용 1', 'password1', 10),
('Bob', '게시물 제목 2', '게시물 내용 2', 'password2', 20),
('Charlie', '게시물 제목 3', '게시물 내용 3', 'password3', 5),
('David', '게시물 제목 4', '게시물 내용 4', 'password4', 15),
('Eve', '게시물 제목 5', '게시물 내용 5', 'password5', 25),
('Frank', '게시물 제목 6', '게시물 내용 6', 'password6', 30),
('Grace', '게시물 제목 7', '게시물 내용 7', 'password7', 12),
('Heidi', '게시물 제목 8', '게시물 내용 8', 'password8', 17),
('Ivy', '게시물 제목 9', '게시물 내용 9', 'password9', 22),
('Jack', '게시물 제목 10', '게시물 내용 10', 'password10', 28),
('Karl', '게시물 제목 11', '게시물 내용 11', 'password11', 32),
('Laura', '게시물 제목 12', '게시물 내용 12', 'password12', 9),
('Mallory', '게시물 제목 13', '게시물 내용 13', 'password13', 7),
('Nancy', '게시물 제목 14', '게시물 내용 14', 'password14', 14),
('Oscar', '게시물 제목 15', '게시물 내용 15', 'password15', 21),
('Peggy', '게시물 제목 16', '게시물 내용 16', 'password16', 24),
('Quinn', '게시물 제목 17', '게시물 내용 17', 'password17', 16),
('Rachel', '게시물 제목 18', '게시물 내용 18', 'password18', 18),
('Sam', '게시물 제목 19', '게시물 내용 19', 'password19', 29),
('Trudy', '게시물 제목 20', '게시물 내용 20', 'password20', 31),
('Uma', '게시물 제목 21', '게시물 내용 21', 'password21', 11),
('Victor', '게시물 제목 22', '게시물 내용 22', 'password22', 26),
('Wendy', '게시물 제목 23', '게시물 내용 23', 'password23', 13),
('Xander', '게시물 제목 24', '게시물 내용 24', 'password24', 19),
('Yvonne', '게시물 제목 25', '게시물 내용 25', 'password25', 33);

-- AddPost 프로시저 호출 예시
CALL AddPost('작성자명', '제목', '내용', '비밀번호', NOW());

-- posts 테이블 조회
SELECT * FROM posts;
