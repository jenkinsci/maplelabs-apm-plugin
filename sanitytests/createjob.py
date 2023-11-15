import requests
import json
import base64  # Import the base64 module
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

else:
    # Handle errors
    print(f"Failed to retrieve Jenkins configuration. HTTP status code: {response.status_code}")
    print(response.text)
session.close()
auth = (username, api_token)

# Job configuration XML
job_config_xml = """
<project>
    <description>My Jenkins Job</description>
    <builders>
        <hudson.tasks.Shell>
            <command>echo "Hello, Jenkins!"</command>
        </hudson.tasks.Shell>
    </builders>
</project>
"""
# Create a new job
job_name = input("Enter Job name")
job_url = f'{jenkins_url}/createItem?name={job_name}'
build_url = f'{jenkins_url}/job/{job_name}/build'
api_url = f"{jenkins_url}/job/{job_name}/api/json"
headers = {
    'Authorization': f'Basic {base64.b64encode(f"{username}:{api_token}".encode()).decode()}',
    'Content-Type': 'application/xml',
}
response = requests.post(job_url, headers=headers, data=job_config_xml)

# Check if the job creation was successful
if response.status_code == 200:
    print(f'Job "{job_name}" created successfully.')
    response1 = requests.post(build_url, auth=auth)
    print(f"Build of job '{job_name}' started successfully.")
    print("Job started successfully")
else:
    print("Job already exists")
    response1 = requests.post(build_url, auth=auth)
    print(f"Build of job '{job_name}' started successfully.")
    print("Job started successfully")

    session = requests.Session()
    session.auth = (username, api_token)
    response2 = session.get(api_url)
    if response2.status_code == 200:
        job_details = response2.json()
        print(f"Last Build Number: {job_details['lastCompletedBuild']['number'] + 1}")
    else:
        print(f"Failed to retrieve job details. Status code: {response2.status_code}")
    session.close()
