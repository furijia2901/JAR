from pathlib import Path
import re, json
root = Path(__file__).resolve().parents[1]
s = (root/'src/client/java/jp/worldjourney/gachafx/GachaFxLibrary.java').read_text(encoding='utf8')
themes = re.findall(r'new Theme\("([a-z]+)"', s)
motions = re.search(r'MOTIONS = List\.of\((.*?)\);', s, re.S).group(1)
motions = re.findall(r'"([a-z]+)"', motions)
ids = [f'{t}_{m}' for t in themes for m in motions]
assert len(themes)==5 and len(motions)==12 and len(ids)==len(set(ids))==60
meta=json.loads((root/'src/main/resources/fabric.mod.json').read_text())
assert meta['id']=='gachafx'
print(f'PASS: {len(themes)} themes x {len(motions)} motions = {len(ids)} IDs; Fabric mod metadata valid')
