create table pgcal02 (
    country_code char(3) not null,
    trd_date  char(8) not null,
    day_of_week char(1) not null,
    legal_holiday char(8) default 'N' not null
);
create unique index pgcal02_pk on pgcal02(country_code,trd_date);