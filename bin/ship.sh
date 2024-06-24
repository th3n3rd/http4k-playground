#!/bin/bash

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" &> /dev/null && pwd)"

echo "Navigating to the root of the project"
cd "$SCRIPT_DIR/.."

echo "Validating the current branch"
CURRENT_BRANCH=$(git branch --show-current)
if [ "$CURRENT_BRANCH" != "main" ]; then
  echo "You have to be on main to ship the changes"
  exit 1
fi

echo "Checking for uncommitted changes"
UNCOMMITTED_CHANGES=$(git status --porcelain | wc -l)
if [ "$UNCOMMITTED_CHANGES" -ne 0 ]; then
  git status
  echo
  echo "^^^You have some uncommitted changes"
  echo
  exit 1
fi

echo "Fetching latest changes (and rebasing on top) from the remote repository..."
git pull -r

./bin/build.sh

echo "Publishing all changes into the remote repository..."
git push

echo "Shipped it!"