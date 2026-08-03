import json
import re

json_path = r'd:\Project\KMP\TaminHamrahCMP\old_android\app\src\main\assets\agent_one_response.json'
kt_path = r'd:\Project\KMP\TaminHamrahCMP\core\core-network\src\commonMain\kotlin\com\tamin\taminhamrah\dataSource\agent\AgentFakeData.kt'

with open(json_path, 'r', encoding='utf-8') as f:
    data = json.load(f)

entities = data['data']['result']['entities']

# Keep max 2 of each key type
key_counts = {}
filtered = []
for entity in entities:
    key = entity['key']
    key_counts[key] = key_counts.get(key, 0) + 1
    if key_counts[key] <= 2:
        filtered.append(entity)

data['data']['result']['entities'] = filtered

# Save the updated JSON
with open(json_path, 'w', encoding='utf-8') as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

print(f"Reduced from {len(entities)} to {len(filtered)} entities")
for k, v in key_counts.items():
    if v > 2:
        print(f"  Removed {v-2} duplicate(s) of '{k}'")

# Also update FAKE_AGENT_ONE_RESPONSE in AgentFakeData.kt
data_str = json.dumps(data['data'], ensure_ascii=False, indent=4)

with open(kt_path, 'r', encoding='utf-8') as f:
    kt_content = f.read()

new_content = re.sub(r'(internal const val FAKE_AGENT_ONE_RESPONSE = \"\"\").*?(\"\"\")', r'\1\n' + data_str.replace('\\', '\\\\') + r'\n\2', kt_content, flags=re.DOTALL)

with open(kt_path, 'w', encoding='utf-8') as f:
    f.write(new_content)

print('Updated AgentFakeData.kt successfully!')
