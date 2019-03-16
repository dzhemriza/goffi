REM File: json-formatter.vbs
REM Script requires javaw.exe to be already provided in the command prompt

Set objShell = CreateObject("Wscript.Shell")
strPath = Wscript.ScriptFullName

Set objFSO = CreateObject("Scripting.FileSystemObject")
Set objFile = objFSO.GetFile(strPath)

strFolder = objFSO.GetParentFolderName(objFile)
strBinFolder = objFSO.GetParentFolderName(strFolder)
strRootFolder = objFSO.GetParentFolderName(strBinFolder)
strLibFolder = strRootFolder & "\lib\json-formatter"

objShell.Run("javaw --module-path """ & strLibFolder & """ --add-modules org.goffi.json.formatter,javafx.controls -m org.goffi.json.formatter/org.goffi.json.formatter.gui.App")
