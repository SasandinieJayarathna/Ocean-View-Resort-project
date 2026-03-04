UPDATE users SET password_hash='$2a$10$95U9YTNkHjTTjyNFW2epZOaG87vizKlKzDVVPONfp7SvA1MnUJfjC' WHERE username='admin';
UPDATE users SET password_hash='$2a$10$ZsDbWJXzUYi5iQ8iSZ7b0ee0NeGMb4Y.GwXAuhv2U.B2VdIyOwURK' WHERE username='staff1';
SELECT username, LEFT(password_hash, 10) as hash_check FROM users;
