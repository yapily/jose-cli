APP_VERSION=`xmllint --xpath '/*[local-name()="project"]/*[local-name()="version"]/text()' pom.xml`
echo $APP_VERSION

mkdir -p releases/jose-${APP_VERSION}

cp bin/jose.sh releases/jose-${APP_VERSION}/jose
cp target/jose-jwkset-cli-${APP_VERSION}.jar releases/jose-${APP_VERSION}/jose-jwkset-cli.jar

cd releases
zip -r jose-cli-${APP_VERSION}.zip jose-${APP_VERSION}/