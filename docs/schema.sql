create table pgcal02 (
    country_code char(3) not null,
    trd_date  char(8) not null,
    day_of_week char(1) not null,
    legal_holiday char(1) default 'N' not null
);
create unique index pgcal02_pk on pgcal02(country_code,trd_date);

create table pgcal03 (
    country_code char(3) not null,
    trd_date  char(8) not null,
    day_of_week char(1) not null,
    legal_holiday char(1) default 'N' not null,
    description  char(100) default ''
);
create unique index pgcal03_pk on pgcal03(country_code,trd_date);