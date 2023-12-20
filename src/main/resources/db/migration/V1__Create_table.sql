create table if not exists news (
                      id bigint primary key auto_increment,
                      title text not null ,
                      content text not null ,
                      link text not null ,
                      created_at timestamp not null default now(),
                      updated_at timestamp not null default now()
);
create table if not exists link_pool (
                           id bigint primary key auto_increment,
                           link text not null,
                           created_at timestamp not null default now(),
                           updated_at timestamp not null default now()
);
create table if not exists filter_pool (
                           id bigint primary key auto_increment,
                           link text not null,
                           created_at timestamp not null default now(),
                           updated_at timestamp not null default now()
);