# 국가별 달력 생성

## 국가별 달력을 생성하기 위해 슼크래핑을 이용함

## 1. 사용법:
```
    java -jar MakeCalendar-1.0.0.jar --ksnet.pginfo.country_code=ALL --ksnet.pginfo.year=2025
```

## 2. 지원국가코드
| 국가코드 | 국가명  |
|------|------|
| KOR  | 대한민국 |
| USA  | 미국   |
| CHN  | 중국   |
| HKG  | 홍콩   |
| SGP  | 싱가포르 |
| JPN  | 일본   |

## 3. 테이블

| 컬럼한글명 | 컬럼명           | 사이즈     | 비고          |
|-------|---------------|---------|-------------|
| 국가코드  | country_code  | char(3) | 지원국가코드      |
 | 날짜    | trd_date      | char(8) | yyyyMMdd    |
| 요일    | day_of_week   | char(1) | 월:0 ~ 일:6   |
| 휴일구분  | legal_holiday | char(1) | 휴일:Y, 영업일:N |

