#!/usr/bin/env bash
set -x
cd "$(dirname "$0")"
export env=${1:-stage}

helm upgrade --install $env payday/ --namespace payday-$env -f payday/$env.yaml
