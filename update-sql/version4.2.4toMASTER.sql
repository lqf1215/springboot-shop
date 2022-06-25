/** 增加签到日期 **/
ALTER TABLE li_user_sign ADD day int DEFAULT NULL COMMENT '签到日 ';
ALTER TABLE li_user_sign DROP INDEX uk_member_day;
ALTER TABLE li_user_sign add unique uk_member_day (user_id, day) COMMENT 'uk_member_day';