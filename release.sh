#!/bin/bash

VERSION_STRING=$1
if [ "$VERSION_STRING" = "" ]; then
  echo "Must pass a version"
  exit 0;
fi
echo "Setting v$VERSION_STRING"
echo $VERSION_STRING > VERSION

echo "## [$VERSION_STRING](https://github.com/Indellient/habitat-jenkins/tree/v$VERSION_STRING) $(date +%m-%d-%Y)" > tmpfile
git log --pretty=format:" - %s" "v$VERSION_STRING"...HEAD >> tmpfile
echo "" >> tmpfile
echo "" >> tmpfile
cat CHANGELOG.md >> tmpfile
mv tmpfile CHANGELOG.md
git add CHANGELOG.md VERSION
if git commit -S -m "Version bump to $VERSION_STRING"; then
  echo "Successfully signed commit"
else
  echo "Failed to gpg sign commit"
  git commit -s -m "Version bump to $VERSION_STRING"
fi

git tag -a -m "Tagging version $VERSION_STRING" "v$VERSION_STRING"
git push origin --tags

sleep 5

REPO="habitat-jenkins"
OWNER="Indellient"
ARTIFACT="./target/com.indellient.habitat.hpi"
./release-asset.sh github_api_token=${GH_API_TOKEN} owner=${OWNER} repo=${REPO} tag=v${VERSION_STRING} filename=${ARTIFACT}
