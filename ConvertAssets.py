import os
import sys
import re

matcher = re.compile("[A-Z]{2,}")


def to_snake_case(string):
    """
    :type string: str
    """
    if string.isupper():
        return string.lower()
    elif string.islower():
        return string
    elif matcher.search(string):
        return string.lower()

    split = list()
    last_index = 0
    for i in range(len(string)):
        if string[i].isupper():
            split.append(string[last_index:i].lower())
            last_index = i
    split.append(string[last_index:len(string)].lower())
    return "_".join(split)


def break_up_lang(string):
    """
    :type string: str
    """
    if string.strip().startswith("#"):
        return string
    broken = string.split("=", 1)
    if len(broken) != 2:
        return string
    lang_parts = broken[0].replace(":", ".:").split(".")
    corrected = ""
    for part in lang_parts:
        snake = to_snake_case(part)
        if part.startswith(":"):
            corrected += snake
        else:
            corrected += "." + snake

    return corrected[1:] + "=" + broken[1]


allConverted = list()

homeDir = os.path.expanduser("~")

directories = os.walk("." if len(sys.argv) < 2 else sys.argv[1])

for (path, dirs, files) in directories:
    for f in files:
        changed = False

        path_to_file = os.path.join(path, f)
        if not f.islower():
            path_to_new = os.path.join(path, to_snake_case(f))
            changed = True
        else:
            path_to_new = path_to_file

        loaded = open(path_to_file)
        data = loaded.read()
        loaded.close()
        if not data.endswith("\n") and not f.endswith(".png"):
            data += "\n"
            changed = True

        if f.endswith(".lang"):
            lines = data.splitlines(True)
            lines = map(break_up_lang, lines)
            data = "".join(lines)
            changed = True

        if changed:
            os.remove(path_to_file)
            new = open(path_to_new, "w")
            new.write(data)
            new.close()
            if path_to_file == path_to_new:
                allConverted.append(path_to_file)
            else:
                allConverted.append(path_to_file + " -> " + path_to_new)

for converted in allConverted:
    print "Converted file", converted