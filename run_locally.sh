set -e
set -x

mvn clean
mvn package

# TODO(chris): Once `gcloud app` launches, use that command.
~/google-cloud-sdk/bin/dev_appserver.sh ./target/flapping-bird-1

