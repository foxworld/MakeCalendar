# ☆ 국가별 달력 생성

## 국가별 달력(휴일포함)을 생성하기 위해 스크래핑을 이용

## 1. 사용법:
```commandline
    java -Dspring.profiles.active=prod -jar MakeCalendar-x.x.x.jar \
        --COUNTRY-CODE=ALL|KOR|USA|JPN|SGP|HKG|CHN \
        --YEAR=2025 \
        [--ONLY-HOLIDAY=true|false]

        Parameters:
        COUNTRY-CODE : ALL, KOR, USA, JPN, SGP, HKG, CHN
        YEAR         : Positive integer (example: 2025)
        ONLY-HOLIDAY : Optional boolean flag (default: false)
```

## 2. 지원국가코드
| 국가코드 |  국가명  |
|:--------:|:--------:|
|   KOR    | 대한민국 |
|   USA    |   미국   |
|   CHN    |   중국   |
|   HKG    |   홍콩   |
|   SGP    | 싱가포르 |
|   JPN    |   일본   |

## 3. 테이블

### 테이블명 : PGCAL02
|  한글명  |    컬럼명     | 사이즈  |       비고        |
|:--------:|:-------------:|:-------:|:-----------------:|
| 국가코드 | country_code  | char(3) | 지원국가코드 참조 |
|   날짜   |   trd_date    | char(8) |     yyyyMMdd      |
|   요일   |  day_of_week  | char(1) |    월:0 ~ 일:6    |
| 휴일구분 | legal_holiday | char(1) | 휴일:Y, 영업일:N  |

