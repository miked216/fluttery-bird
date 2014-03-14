# Manually update:
# ../sdks/java_sdk/appengine-java-sdk-1.9.0/bin/appcfg.sh --oauth2 update ./target/flapping-bird-1

set -e
set -x

mvn clean
mvn package
../sdks/java_sdk/appengine-java-sdk-1.9.0/bin/dev_appserver.sh ./target/flapping-bird-1

