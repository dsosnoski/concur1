mvn scala:run -q -Dlauncher=basepool -DaddArgs=$1
mvn scala:run -q -Dlauncher=forkjoin -DaddArgs=$1
mvn scala:run -q -Dlauncher=parcol -DaddArgs=$1
mvn scala:run -q -Dlauncher=dirblock -DaddArgs=$1
mvn scala:run -q -Dlauncher=futurefold -DaddArgs=$1
mvn scala:run -q -Dlauncher=recursplit -DaddArgs=$1

