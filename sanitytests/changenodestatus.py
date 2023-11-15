import requests
import sys
from requests.auth import HTTPBasicAuth

def get_node_status(jenkins_url, username, api_token, node_name):
    # URL to get the current node status
    url = f'{jenkins_url}/computer/{node_name}/api/json'

    response = requests.get(
        url,
        auth=HTTPBasicAuth(username, api_token)
    )

    if response.status_code == 200:
        data = response.json()
        offline = data.get('offline', True)

        if offline:
            return "Offline"
        else:
            return "Online"
    else:
        return f"Failed to retrieve node status. Status code: {response.status_code}"

def mark_node_offline(jenkins_url, username, api_token, node_name):
    # Prepare the JSON payload to mark the node as offline
    offline_payload = {
        "offlineMessage": "Node marked as offline for maintenance",
        "offline": True,
    }

    # URL for marking the node as offline
    url = f'{jenkins_url}/computer/{node_name}/toggleOffline'

    # Send an HTTP POST request to mark the node as offline
    response = requests.post(
        url,
        json=offline_payload,
        auth=HTTPBasicAuth(username, api_token)
    )

    if response.status_code == 200:
        print(f"Node '{node_name}' has been marked as offline.")
    else:
        print(f"Failed to mark the node as offline. Status code: {response.status_code}")


def mark_node_online(jenkins_url, username, api_token, node_name):
    # Prepare the JSON payload to mark the node as online
    online_payload = {
        "offlineMessage": "",
        "offline": False,
    }

    # URL for marking the node as online
    url = f'{jenkins_url}/computer/{node_name}/toggleOffline'

    # Send an HTTP POST request to mark the node as online
    response = requests.post(
        url,
        json=online_payload,
        auth=HTTPBasicAuth(username, api_token)
    )

    if response.status_code == 200:
        print(f"Node '{node_name}' has been marked as online.")
    else:
        print(f"Failed to mark the node as online. Status code: {response.status_code}")

if __name__ == "__main__":
    config_file_url = 'https://raw.githubusercontent.com/maplelabs/apm-jenkins-plugin/Bhanuprakash-ML-V2/sanitytests/jenkins_config.txt'
    session = requests.Session()
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
        print(f"Failed to retrieve Jenkins configuration. HTTP status code: {response.status_code}")
        print(response.text)
    session.close()
    node_name = sys.argv[1]
    make=sys.argv[2]

    status = get_node_status(jenkins_url, username, api_token, node_name)
    if status == "Offline" and make == "online":
        print(f"Node '{node_name}' is currently offline and making it online.")
        mark_node_online(jenkins_url, username, api_token, node_name)
    elif status == "Online" and make == "offline":
        print(f"Node '{node_name}' is currently online and making it offline.")
        mark_node_offline(jenkins_url, username, api_token, node_name)
    else:
        print(f"Node '{node_name}' is currently {status}.")
