-- test data
insert into account (username, email, password, role, is_Active) values ('admin', 'admin@aj.com', 'admin', 'ADMIN', true);
insert into account (username, email, password, role, is_Active) values ('sa', 'sa@sa.com', 'sa', 'USER', true);
insert into ticket (subject, description, created_Date, last_Updated_Date, status, account_id) values ('cant login to acc service', 'same as title<br>< Sat Nov 18 11:48:38 IST 2023 ><br>fixed', '2023-11-18T06:00:09.277+00:00', '2023-11-18T06:18:38.102+00:00', 'CLOSED', 1);
insert into ticket (subject, description, created_Date, last_Updated_Date, status, account_id) values ('cant login to acc service', 'same as title', '2023-11-18T06:00:09.277+00:00', '2023-11-18T06:00:09.277+00:00', 'OPEN', 1);
