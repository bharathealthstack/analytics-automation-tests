#!/usr/bin/env bash
set -e
args=("$@")

function installTestDependencies {
  set -e
  echo "Printing installTestDependencies"
}

function sendMail {
    set -e
    echo "Printing Send Mail"
    echo "########################"
    echo "------------------------"
    echo "Removing report"
    echo "########################"
    echo "------------------------"
}

function runTest {
    set -e
    echo "RDS_DB_NAME"
    echo $RDS_DB_NAME
    echo "Printing service"
    echo ${args[1]}
    echo "########################"
    installTestDependencies
    echo "runTest"
    mvn clean test -DtestSuite=sanitysuite -X
    echo "########################"
    echo "Status = ${status}"
    echo "-----------------------"
    if [ $status -ne 0 ]
    then
        echo "sendMail"
    fi
    echo "------------------------"
}

case "$1" in
    rdstest) runTest;;
esac
