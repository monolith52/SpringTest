create table `book` (
`bookId` integer,
`title` text not null,
`siteId` varchar(16) not null,
`fileName` text,
`thumbnail` blob,
`md5` varchar(32),
primary key(`bookId`)
);
create index book_siteId on `book`(`siteId`);
create index book_md5 on `book`(`md5`);
