#!/bin/sh
echo java ${JAVA_OPTS} -cp app:app/lib/* ${MAIN_CLASS} ${@}
ls -a && cd app && ls -a && cd lib && ls -a
exec java ${JAVA_OPTS} -cp /app:/app/lib/ ${MAIN_CLASS}
