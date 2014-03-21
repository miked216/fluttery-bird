set -e
set -x

mvn clean
mvn package

~/google-cloud-sdk/bin/dev_appserver.sh ./target/fluttery-bird-1

