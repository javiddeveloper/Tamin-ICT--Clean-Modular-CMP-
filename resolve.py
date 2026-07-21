import re

with open('shared/src/commonMain/kotlin/com/tamin/taminhamrah/ui/navigation/TaminHamrahNavGraph.kt', 'r', encoding='utf-8') as f:
    text = f.read()

pattern = r'<<<<<<< HEAD\n(.*?)\n=======\n(.*?)\n>>>>>>> origin/develop'

def repl(m):
    head = m.group(1).strip()
    dev = m.group(2).strip()
    
    head_setup = head.split('Scaffold(')[0].strip()
    head_fab = 'floatingActionButton' + head.split('floatingActionButton')[1].strip()
    
    dev_setup = dev.split('Scaffold(')[0].strip()
    dev_scaffold = 'Scaffold(' + dev.split('Scaffold(')[1].strip()
    
    # ensure proper comma
    if not head_fab.endswith(','):
        head_fab += ','
        
    merged_scaffold = dev_scaffold.replace('bottomBar = {', head_fab + '\n        bottomBar = {')
    
    return head_setup + '\n\n    ' + dev_setup + '\n    ' + merged_scaffold

merged_text = re.sub(pattern, repl, text, flags=re.DOTALL)

with open('shared/src/commonMain/kotlin/com/tamin/taminhamrah/ui/navigation/TaminHamrahNavGraph.kt', 'w', encoding='utf-8') as f:
    f.write(merged_text)
print('Done!')
