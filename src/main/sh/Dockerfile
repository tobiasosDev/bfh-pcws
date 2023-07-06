# Use the official Ubuntu image from Docker Hub as
# a base image
FROM ubuntu:22.04

# Execute next commands in the directory /workspace
WORKDIR .

# Just in case the script doesn't have the executable bit set
RUN chmod +x ./script.sh

# Run the script when starting the container
CMD [ "./script.sh" ]