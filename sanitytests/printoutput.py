import sys

import requests
from requests.auth import HTTPBasicAuth

session = requests.Session()
config_file_url = 'https://raw.githubusercontent.com/maplelabs/apm-jenkins-plugin/Bhanuprakash-ML-V2/sanitytests/jenkins_config.txt'

response = session.get(config_file_url)

if response.status_code == 200:
    config_content = response.text
    jenkins_url = ''
    username = ''

    # Parse the content to extract Jenkins URL and Username
    for line in config_content.split('\n'):
        if line.startswith('Jenkins URL'):
            jenkins_url = line.split('=')[1].strip()
        elif line.startswith('Username'):
            username = line.split('=')[1].strip()
        elif line.startswith('api'):
            api_token = line.split('=')[1].strip()
        elif line.startswith('jobname'):
            job_name = line.split('=')[1].strip()

else:
    # Handle errors
    print(f"Failed to retrieve Jenkins configuration. HTTP status code: {response.status_code}")
    print(response.text)

session.close()

latest_build_url = f'{jenkins_url}/job/{job_name}/lastBuild/buildNumber'

# Send an HTTP GET request to get the latest build number with basic authentication
auth = HTTPBasicAuth(username, api_token)
response = requests.get(latest_build_url, auth=auth)

if response.status_code == 200:
    latest_build_number = int(response.text)

    # Define the API endpoint for the console output of the latest build
    console_output_url = f'{jenkins_url}/job/{job_name}/{latest_build_number}/consoleText'

    # Send an HTTP GET request to the console output URL with basic authentication
    response = requests.get(console_output_url, auth=auth)

    if response.status_code == 200:
        # Successful response
        console_output = response.text

        # Save the console output to a local text file (write mode)
        with open(f'console_output_latest_build.txt', 'w') as file:
            file.write(console_output)
    else:
        # Handle errors when retrieving the console output
        print(f"Failed to retrieve console output. HTTP status code: {response.status_code}")
        print(response.text)
else:
    # Handle errors when retrieving the latest build number
    print(f"Failed to retrieve latest build number. HTTP status code: {response.status_code}")
    print(response.text)
