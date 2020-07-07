| |Current Status|
|---|---|
|Build|[![Build Status](https://img.shields.io/endpoint.svg?url=https%3A%2F%2Factions-badge.atrox.dev%2Fyapily%2Fjose-cli%2Fbadge%3Fref%3Dmaster&style=flat)](https://actions-badge.atrox.dev/yapily/jose-cli/goto?ref=master)|
|Code coverage|[![codecov](https://codecov.io/gh/yapily/jose-cli/branch/master/graph/badge.svg)](https://codecov.io/gh/yapily/jose-cli)
|Latest release|![GitHub release (latest by date)](https://img.shields.io/github/v/release/yapily/jose-cli)|
|License|![license](https://img.shields.io/github/license/yapily/jose-cli)|


# JOSE CLI
CLI based on the JOSE standard, to help you manipulate JWKs

## Requirements

You will need to have Java 11+ installed

## How to install

Download the latest zip version (which will be referenced as $latest_version from this point onwards) from https://github.com/yapily/jose-cli/releases


```
cd /tmp
wget https://github.com/yapily/jose-cli/releases/download/jose-cli-$latest_version/jose-cli-$latest_version.zip
unzip jose-cli-$latest_version.zip
cp -rf jose-$latest_version/* /usr/local/bin/
chmod +x /usr/local/bin/jose
cd -
```

## How to use it

To create a new set of keys, run:

```
jose jwks-sets init -o /tmp/keys 
```

To rotate a set of existing keys, run:

```
jose jwks-sets rotate -k /tmp/keys -o /tmp/keys
```

