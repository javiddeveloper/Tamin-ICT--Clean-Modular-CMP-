import re
import json

with open(r'core\core-network\src\commonMain\kotlin\com\tamin\taminhamrah\dataSource\commonSource\MockMenuData.kt', 'r', encoding='utf-8') as f:
    content = f.read()

json_data = []
lines = [l.strip() for l in content.split('\n') if l.strip().startswith('MainServiceDto')]

for line in lines:
    obj = {}
    
    # parse id
    m_id = re.search(r'id\s*=\s*(\d+)', line)
    if m_id: obj['id'] = int(m_id.group(1))
        
    m_name = re.search(r'name\s*=\s*"([^"]+)"', line)
    if m_name: obj['name'] = m_name.group(1)
        
    m_icon = re.search(r'icon\s*=\s*"([^"]+)"', line)
    if m_icon: obj['icon'] = m_icon.group(1)
        
    m_status = re.search(r'status\s*=\s*MenuServiceStatus\.([A-Z_]+)', line)
    if m_status: obj['status'] = m_status.group(1)
        
    m_msg = re.search(r'message\s*=\s*"([^"]+)"', line)
    if m_msg: obj['message'] = m_msg.group(1)
        
    m_url = re.search(r'url\s*=\s*"([^"]+)"', line)
    if m_url: obj['url'] = m_url.group(1)
        
    m_roles = re.search(r'showRole\s*=\s*listOf\(([\d, ]+)\)', line)
    if m_roles:
        obj['showRole'] = [int(r.strip()) for r in m_roles.group(1).split(',')]
            
    if 'active' not in obj:
        obj['active'] = True
    if obj.get('status') in ['DISABLED', 'COMPLETELY_DISABLED']:
        obj['active'] = False
        
    json_data.append(obj)

with open('menu.json', 'w', encoding='utf-8') as f:
    json.dump({'data': json_data}, f, ensure_ascii=False, indent=4)
