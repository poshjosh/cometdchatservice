#!/bin/sh
echo 'java ${JAVA_OPTS} -cp app:app/lib/* ${MAIN_CLASS} ${@}'
exec java ${JAVA_OPTS} -cp app:app/lib/* ${MAIN_CLASS}
