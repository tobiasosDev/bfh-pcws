#!/bin/sh

readonly DATALOG_SERVICE_URL=${PCWS_DATALOG_SERVICE_URL:-'https://bfh-paketblitz-datalog-service.herokuapp.com'}
readonly DATALOG_DIRECTORY=${PCWS_DATALOG_DIRECTORY:-'.'}

function transfer_log() {
  local log_file=$1
  echo "Transferring ${log_file}"
  curl --fail -X POST ${DATALOG_SERVICE_URL}/entries -d @${log_file} --header "Content-Type: application/json"
  local curl_status=$?
  if [[ ${curl_status} -eq 0 ]]; then
    echo "Successfully transferred ${log_file}"
    rm -f ${log_file}
  else
    echo "Failed to transfer ${log_file}"
  fi
}

function transfer_logs() {
  local log_directory=$1
  gsutil ls gs://pcws-log-bucket
  for google_file in gsutil ls gs://pcws-log-bucket; do
    gsutil cp ${google_file} ${log_directory}
    gsutil rm ${google_file}
  done
  for log_file in ${log_directory}/*.json; do
    transfer_log ${log_file}
  done
}

function init_transfer() {
    local log_directory=${DATALOG_DIRECTORY}
    echo "Starting datalog transfer from ${log_directory}"
    transfer_logs ${log_directory}
    echo "Finished datalog transfer from ${log_directory}"
}

function main() {
  # run init_transfer every minute
  while true; do
    init_transfer
    sleep 60
  done
}

main $@