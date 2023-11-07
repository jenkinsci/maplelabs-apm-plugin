import requests
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
        elif line.startswith('Password'):
            password = line.split('=')[1].strip()
else:
    # Handle errors
    print(f"Failed to retrieve Jenkins configuration. HTTP status code: {response.status_code}")
    print(response.text)

session.close()

url=f'{jenkins_url}/manage/log/snappyflow'
# Provide your username and password for basic authentication
response = requests.get(url, auth=(username, password))

if response.status_code == 200:
    data = response.text
    lines = data.split('\n')

    # Initialize variables to track the last BuildEvent data and response code
    last_build_event_data = None
    last_response_code = None

    # Iterate through the lines to find and store the last BuildEvent data and response code
    for line in lines:
        if '"_documentType":"BuildEvent"' in line:
            last_build_event_data = line
        elif "Response Code : 201" in line:
            last_response_code = line

    if last_build_event_data and last_response_code:
        print(last_build_event_data)
        print(last_response_code)
        print("Test case passed")
    else:
        print("No data found for 'BuildEvent' with response code 201.")
else:
    print(f"Failed to retrieve data. Status code: {response.status_code}")
