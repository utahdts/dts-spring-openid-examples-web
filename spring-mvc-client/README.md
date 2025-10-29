# OpenId Connect examples

## Spring 6 MVC Client
An OIDC client application.

Uses EnableOAuth2Client annotation provided by Spring Security OAuth2

###  H2 database changes
Modify resources/db/data.sql to add your name and umd email

i.e.

INSERT INTO MEMBER ("Email", "Inactive", "Admin", "Name", "UMDUniqueId", "RoleId") VALUES ('yourEmail@utah.gov', 0, 1, 'Your Name', '', 4);

:thumbsup:
