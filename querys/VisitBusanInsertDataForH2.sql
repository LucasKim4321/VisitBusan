-- Visit Busan
-- H2DB용 초기 데이터
-- H2DB에서 SYSDATE()가 안먹혀서 NOW()로 바꿈
-- NOW() == CURRENT_TIMESTAMP() != SYSDATE()

-- 선택된 데이터베이스의 테이블 목록 표시
-- SHOW TABLES;


-- ------------------------------
--            멤버
-- ------------------------------

-- 비밀번호 1234

INSERT INTO member
(address, email, NAME, PASSWORD, user_id)
VALUES
('root','root@test.com', 'root', '$2a$10$WnW8tFgaxGe.CHuv2UjrG.1HmWPkayetVCKkXLVRkplvWjr7T7R4O', 'root'),
('admin','admin@test.com', 'admin', '$2a$10$WnW8tFgaxGe.CHuv2UjrG.1HmWPkayetVCKkXLVRkplvWjr7T7R4O', 'admin'),
('user','user@test.com', 'user', '$2a$10$WnW8tFgaxGe.CHuv2UjrG.1HmWPkayetVCKkXLVRkplvWjr7T7R4O', 'user'),
('test','test@test.com', 'test', '$2a$10$WnW8tFgaxGe.CHuv2UjrG.1HmWPkayetVCKkXLVRkplvWjr7T7R4O', 'test');
SHOW COLUMNS FROM member;
SELECT * FROM member;


-- ------------------------------
--           멤버 권한
-- ------------------------------

-- 권한
-- user=0 admin=1 root=2

INSERT INTO member_role_set
(member_member_id,role_set)
VALUES
(1,2),
(1,1),
(2,1),
(3,0);
SHOW COLUMNS FROM member_role_set;
SELECT * FROM member_role_set;


-- ------------------------------
--            게시판
-- ------------------------------

-- 카테고리

-- 관리자 게시판 : 
--  - 여행정보게시판(travelInfo) : 명소(place), 음식(food), 숙박(accommodation)
--  - 여행추천게시판(travelRecommend) : 일정여행(), 테마여행()
--  - 여행가이드게시판(travelGuide) : 가이드(guide), 여행준비(preparation)
--  - 축제게시판(festivalBoard) : 축제행사(festival), 공연전시(performance)

-- 유저 게시판 :
--  - 유저게시판(userBoard) : 여행정보(information), 여행일정(schedule), 후기(review)

INSERT INTO board
(category,title,content,writer,writer_id,reg_date,mod_date)
VALUES
('festival','title1','content1','admin','admin',NOW(),NOW()),
('festival','title2','content2','admin','admin',NOW(),NOW()),
('performance','title3','content3','admin','admin',NOW(),NOW()),
('performance','title4','content4','admin','admin',NOW(),NOW()),
('place','title5','content5','admin','admin',NOW(),NOW()),
('place','title6','content6','admin','admin',NOW(),NOW()),
('food','title7','content7','admin','admin',NOW(),NOW()),
('food','title8','content8','admin','admin',NOW(),NOW()),
('accommodation','title9','content9','admin','admin',NOW(),NOW()),
('accommodation','title10','content10','admin','admin',NOW(),NOW()),
('guide','title11','content11','admin','admin',NOW(),NOW()),
('guide','title12','content12','admin','admin',NOW(),NOW()),
('preparation','title13','content13','admin','admin',NOW(),NOW()),
('preparation','title14','content14','admin','admin',NOW(),NOW()),
('information','title15','content15','user','user',NOW(),NOW()),
('information','title16','content16','user','user',NOW(),NOW()),
('schedule','title17','content17','user','user',NOW(),NOW()),
('schedule','title18','content18','user','user',NOW(),NOW()),
('review','title19','content19','user','user',NOW(),NOW()),
('review','title20','content20','user','user',NOW(),NOW());
SHOW COLUMNS FROM board;
SELECT * FROM board;

INSERT INTO board
(category,title,content,writer,writer_id,reg_date,mod_date)
VALUES
('review','부산 시티투어버스 환승 그린 오렌지라인 송도해수욕장과 암남공원','부산여행! 뚜벅이 아니더라도 하루쯤은 시티투어버스에 몸을 맡기고 운전, 주차따위 걱정없이 길맥도 가능한 ㅋㅋㅋ 코스를 추천드립니다. 너무 좋았기 때문에 다음 여행에도 기차여행을 예약해놓은 상태!! 매번 남편, 아빠만 고생하는 것 같지만 부산 시티투어버스 타면 아빠도 매우 즐거움! 부산여행! 뚜벅이 아니더라도 하루쯤은 시티투어버스에 몸을 맡기고 운전, 주차따위 걱정없이 길맥도 가능한 ㅋㅋㅋ 코스를 추천드립니다. 너무 좋았기 때문에 다음 여행에도 기차여행을 예약해놓은 상태!! 매번 남편, 아빠만 고생하는 것 같지만 부산 시티투어버스 타면 아빠도 매우 즐거움! 부산여행! 뚜벅이 아니더라도 하루쯤은 시티투어버스에 몸을 맡기고 운전, 주차따위 걱정없이 길맥도 가능한 ㅋㅋㅋ 코스를 추천드립니다. 너무 좋았기 때문에 다음 여행에도 기차여행을 예약해놓은 상태!! 매번 남편, 아빠만 고생하는 것 같지만 부산 시티투어버스 타면 아빠도 매우 즐거움!','test','test',NOW(),NOW());


-- ------------------------------
--            댓글
-- ------------------------------

INSERT INTO reply
(board_id,reply_text,replier,replier_id,reg_date,mod_date)
VALUES
(1,'reply_text1','user','user',NOW(),NOW()),
(3,'reply_text2','user','user',NOW(),NOW()),
(5,'reply_text3','user','user',NOW(),NOW()),
(7,'reply_text4','user','user',NOW(),NOW()),
(9,'reply_text5','user','user',NOW(),NOW()),
(11,'reply_text6','user','user',NOW(),NOW()),
(13,'reply_text7','user','user',NOW(),NOW()),
(15,'reply_text8','user','user',NOW(),NOW()),
(17,'reply_text9','user','user',NOW(),NOW()),
(19,'reply_text10','user','user',NOW(),NOW());
SHOW COLUMNS FROM reply;
SELECT * FROM reply;

INSERT INTO reply(board_id,reply_text,replier,replier_id,reg_date,mod_date)VALUES(11,'reply_text1','user','user',NOW(),NOW());


-- ------------------------------
--         게시글 좋아요
-- ------------------------------

INSERT INTO board_like
(board_id,user_id,reg_date)
VALUES
(1,'user',NOW()),
(3,'user',NOW()),
(5,'user',NOW()),
(7,'user',NOW()),
(9,'user',NOW()),
(1,'user2',NOW()),
(3,'user2',NOW()),
(5,'user2',NOW()),
(7,'user2',NOW()),
(9,'user2',NOW());
SHOW COLUMNS FROM board_like;
SELECT * FROM board_like;

INSERT INTO board_like(board_id,user_id,reg_date)VALUES(16,'user',NOW());

