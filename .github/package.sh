mkdir -p release/jose
cp bin/jose.sh release/jose

APP_VERSION=`xmllint --xpath '/*[local-name()="project"]/*[local-name()="version"]/text()' pom.xml`
echo $APP_VERSION
cp target/jose-jwkset-cli-${APP_VERSION}.jar release/jose/jose-jwkset-cli.jar