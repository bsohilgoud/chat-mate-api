COPY users(display_name, username, user_id,	password, auth_provider, google_id)
FROM 'D:\\Projects\\chat_mate_project\\chat-mate-api\\src\\main\\resources\\users.csv'
DELIMITER ','
CSV HEADER;

COPY online_status(user_id, status, last_seen)
FROM 'D:\\Projects\\chat_mate_project\\chat-mate-api\\src\\main\\resources\\online_status.csv'
DELIMITER ','
CSV HEADER;

COPY messages(id, sender_id, receiver_id, content, content_type, timestamp, status)
FROM 'D:\\Projects\\chat_mate_project\\chat-mate-api\\src\\main\\resources\\messages.csv'
DELIMITER ','
CSV HEADER;