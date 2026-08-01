import json
import re

json_path = r'd:\Project\KMP\TaminHamrahCMP\old_android\app\src\main\assets\agent_one_response.json'
kt_path = r'd:\Project\KMP\TaminHamrahCMP\core\core-network\src\commonMain\kotlin\com\tamin\taminhamrah\dataSource\agent\AgentFakeData.kt'

with open(json_path, 'r', encoding='utf-8') as f:
    data = json.load(f)

# The FAKE_AGENT_ONE_RESPONSE expects the content of the 'data' field
data_str = json.dumps(data['data'], ensure_ascii=False, indent=4)

with open(kt_path, 'r', encoding='utf-8') as f:
    kt_content = f.read()

# Replace the FAKE_AGENT_ONE_RESPONSE
new_content = re.sub(r'(internal const val FAKE_AGENT_ONE_RESPONSE = \"\"\").*?(\"\"\")', r'\1\n' + data_str.replace('\\', '\\\\') + r'\n\2', kt_content, flags=re.DOTALL)

with open(kt_path, 'w', encoding='utf-8') as f:
    f.write(new_content)

print('Updated AgentFakeData.kt successfully!')
