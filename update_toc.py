import os

def find_markdown_files(base_dir):
    markdown_files = {}
    for root, dirs, files in os.walk(base_dir):
        # Ignorar as pastas node_modules e .venv
        if 'node_modules' in root.split(os.sep) or '.venv' in root.split(os.sep):
            continue
        for file in files:
            if file.endswith('.md'):
                # Obtém o caminho relativo da pasta
                folder = os.path.relpath(root, base_dir)
                if folder == '.':
                    folder = ''  # Para arquivos na raiz
                if folder not in markdown_files:
                    markdown_files[folder] = []
                # Adiciona o arquivo à lista da pasta correspondente
                markdown_files[folder].append(os.path.join(root, file))  # Armazenamos o caminho completo
    return markdown_files

def get_title_from_markdown(file_path):
    """Lê o arquivo Markdown e retorna o primeiro título encontrado."""
    with open(file_path, 'r', encoding='utf-8') as file:
        for line in file:
            if line.startswith('# '):  # Verifica se a linha começa com um título
                return line[2:].strip()  # Retorna o título sem o '# '
    return os.path.basename(file_path)  # Retorna o nome do arquivo se não encontrar título

def generate_menu(markdown_files):
    menu_lines = []
    for folder, files in sorted(markdown_files.items()):
        # Calcula o nível de indentação
        indent_level = folder.count(os.sep)  # Conta o número de separadores de caminho
        indent = '  ' * indent_level  # Define a indentação com base no nível

        # Extrai apenas o nome da pasta final
        folder_name = os.path.basename(folder) if folder else 'Raiz'
        menu_lines.append(f"{indent}* {folder_name}\n")
        
        for file in sorted(files):
            title = get_title_from_markdown(file)  # Obtém o título do arquivo Markdown
            # Cria um link relativo
            relative_path = os.path.relpath(file, start=os.path.dirname(__file__)).replace(os.sep, '/')
            menu_lines.append(f"{indent}  * [{title}]({relative_path})\n")  # Usa o título no link
        menu_lines.append("\n")  # Adiciona uma linha em branco para separação
    return menu_lines

def update_readme(menu):
    readme_path = os.path.join(os.path.dirname(__file__), 'README.md')
    
    # Lê o conteúdo atual do README.md
    with open(readme_path, 'r', encoding='utf-8') as readme:
        content = readme.readlines()
    
    # Adiciona os links ao final do README.md
    content.append("\n## Links para arquivos Markdown\n")
    content.extend(menu)
    
    # Escreve o conteúdo atualizado no README.md
    with open(readme_path, 'w', encoding='utf-8') as readme:
        readme.writelines(content)

if __name__ == "__main__":
    base_directory = os.path.dirname(__file__)  # Diretório base é o da execução do script
    markdown_files = find_markdown_files(base_directory)
    menu = generate_menu(markdown_files)
    update_readme(menu)
    print("README.md atualizado com o menu de arquivos Markdown.")