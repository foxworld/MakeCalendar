#! /usr/bin/sh
export LANG=ko_KR.euckr
export JAVA_BIN=/usr/java8_64/java
export HOME_PATH=/home/pgims/pginfo/bin
export JAR_BIN=MakeCalendar-0.0.1.jar
TODAY=$(date +%Y%m%d)

# ??? ? ???? ???? ??
print_usage() {
  echo ""
  echo "[make_calendar ???]"
  echo "sh make_calendar.sh [YEAR] [ALL|KOR|USA|JPN|HGK|CHN] [ONLY-HOLIDAY]"
  echo "??: sh make_calendar.sh 2026 ALL"
  echo "??: sh make_calendar.sh 2026 KOR"
  echo "??: sh make_calendar.sh 2026 KOR ONLY-HOLIDAY"
}

# ???? ??? 2? ????? ???(-h) ?? ? ??? ?? ? ??
if [ $# -lt 2 ] || [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
  print_usage
  exit 0
fi

# ???? ?? ??
YEAR="$1"
COUNTRY_CODE="$2"

# 3?? ????(ONLY-HOLIDAY) ?? (???: false)
ONLY_HOLIDAY="false"

if [ "$3" = "ONLY-HOLIDAY" ] || [ "$3" = "true" ]; then
  ONLY_HOLIDAY="true"
fi

print ">> ${YEAR}?? [${COUNTRY}] ?? ?? ????? ?????..."

echo "TODAY=${TODAY}"
echo "YEAR=${YYYY}"
echo "COUNTRY_CODE=${COUNTRY_CODE}"
echo "ONLY_HOLIDAY=${ONLY_HOLIDAY}"
${JAVA_BIN} -Dspring.profiles.active=dev -jar ${HOME_PATH}/${JAR_BIN} --YEAR="${YEAR}" --COUNTRY-CODE="${COUNTRY_CODE}" --ONLY-HOLIDAY=${ONLY_HOLIDAY}
RTN=$?
if [ ${RTN} -eq 0 ]; then
  exit 1
else
  exit 0
fi
