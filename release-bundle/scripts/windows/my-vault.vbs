REM File: text-encoder.vbs
REM Script requires javaw.exe to be already provided in the command prompt

Set objShell = CreateObject("Wscript.Shell")
strPath = Wscript.ScriptFullName

Set objFSO = CreateObject("Scripting.FileSystemObject")
Set objFile = objFSO.GetFile(strPath)

strFolder = objFSO.GetParentFolderName(objFile)
strBinFolder = objFSO.GetParentFolderName(strFolder)
strRootFolder = objFSO.GetParentFolderName(strBinFolder)
strLibFolder = strRootFolder & "\lib\my-vault"

objShell.Run("javaw --module-path """ & strLibFolder & """ --add-modules org.goffi.my.vault,javafx.controls,java.sql -m org.goffi.my.vault/org.goffi.my.vault.gui.App")
