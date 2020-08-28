#!/bin/bash
pods=$(kubectl get jobs -n console-prod| grep "-setup-mqa-assessment-v1     0/1")

for pod in $pods 
do
	podInfo=$(echo "$pod" | grep "setup-mqa-assessment")
       if [ -n "$podInfo" ]
       then
 	    echo "RERUNING JOB $podInfo"
	    kubectl -n console-prod get job "$podInfo" -o json | jq 'del(.spec.selector)' | jq 'del(.spec.template.metadata.labels)' | kubectl replace --force -f -
       fi 
done
