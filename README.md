# JOSE CLI
CLI based on the standard JOSE, to help you manipulate JWKs

## Requirements

You will need to have Java 11+ installed

## How to install

Find the latest zip version (lets call it $latest_version from now) from https://github.com/yapily/jose-cli/releases


```
cd /tmp
wget https://github.com/yapily/jose-cli/releases/download/jose-jwkset-cli-$latest_version/jose-cli-$latest_version.zip
unzip jose-cli-$latest_version.zip
cp -rf jose-$latest_version/* /usr/local/bin/
chmod +x /usr/local/bin/jose
cd -
```

## How to use it

The first time, you will want to initialise a set of keys.

```
jose jwks-sets init -o /tmp/keys 
```

To rotate the keys, run

```
jose jwks-sets rotate -k /tmp/keys -o /tmp/keys
```

