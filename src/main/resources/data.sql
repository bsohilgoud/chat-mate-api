COPY users(auth_provider,full_name,provider_id,password,user_id,username,created_at,updated_at)
FROM 'D:\\Projects\\chat_mate_project\\data\\users.csv'
DELIMITER ','
CSV HEADER;

COPY online_status(last_seen,status, user_id)
FROM 'D:\\Projects\\chat_mate_project\\data\\online_status.csv'
DELIMITER ','
CSV HEADER;

COPY media(id,name,size,type,url)
FROM 'D:\\Projects\\chat_mate_project\\data\\media.csv'
DELIMITER ','
CSV HEADER;

COPY messages(id, timestamp, content,content_type,receiver_id,sender_id,status,media_id)
FROM 'D:\\Projects\\chat_mate_project\\data\\messages.csv'
DELIMITER ','
CSV HEADER;