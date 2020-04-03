#!/bin/sh
if [ "$DEBUG" = true ] ; then
    echo 'ls -a' && ls -a
    echo 'cd app' && cd app 
    echo 'ls -a' && ls -a 
    echo 'cd lib' && cd lib 
    echo 'ls -a' && ls -a
    echo 'cd .. && cd ..'
    cd .. && cd ..
fi
echo "Additional java options = ${JAVA_OPTS} ${@}"
echo "Command line arguments = ${@}"
exec java -Djava.security.egd=file:/dev/./urandom \
        -XX:TieredStopAtLevel=1 \
        -XX:+UnlockExperimentalVMOptions \
        -XX:+UseCGroupMemoryLimitForHeap \
        -noverify \
        ${JAVA_OPTS} -cp app:app/lib/* ${MAIN_CLASS} ${@}
