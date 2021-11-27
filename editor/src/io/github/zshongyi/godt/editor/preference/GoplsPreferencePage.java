/**
 * 
 */
package io.github.zshongyi.godt.editor.preference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import io.github.zshongyi.godt.editor.ui.Messages;

/**
 * @author zshongyi
 *
 */
public class GoplsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * 
	 */
	public GoplsPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		this.setPreferenceStore(GoplsPreferencePlugin.getPlugin().getPreferenceStore());
	}

	@Override
	public void init(IWorkbench arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createFieldEditors() {

		FileFieldEditor goplsPath = new FileFieldEditor(GoplsPreferenceConstants.GOPLS_PATH, Messages.goplsPathLabel,
				true, getFieldEditorParent()) {

			@Override
			protected void fireValueChanged(String property, Object oldValue, Object newValue) {
				// TODO Auto-generated method stub
				super.fireValueChanged(property, oldValue, newValue);
				String filePath = newValue.toString();
				if (filePath != "") {
					ProcessBuilder processBuilder = new ProcessBuilder();
					processBuilder.command(filePath, "version");
					try {
						Process process = processBuilder.start();
						BufferedReader bufferedReader = new BufferedReader(
								new InputStreamReader(process.getInputStream()));
						String firstLine = bufferedReader.readLine();
						Pattern pattern = Pattern.compile("^golang.org/x/tools/gopls v([0-9]+\\.)*[0-9]+");
						Matcher matcher = pattern.matcher(firstLine);
						if (matcher.find()) {
							setValid(true);
						} else {
							MessageDialog.openError(null, "Illegal File", "Not an available gopls binary file.");
							setValid(false);
						}
					} catch (IOException e) {
						MessageDialog.openError(null, "Illegal File", e.getMessage());
						e.printStackTrace();
						setValid(false);
					}
				} else {
					setValid(true);
				}
				refreshValidState();
			}
		};

		goplsPath.setFileExtensions(new String[] { getBinaryName() });
		this.addField(goplsPath);

		BooleanFieldEditor enableGodtDeclaration = new BooleanFieldEditor(GoplsPreferenceConstants.GODT_DECLARATION,
				Messages.enableGodtDeclarationLabel, getFieldEditorParent());
		this.addField(enableGodtDeclaration);

	}

	public static String getBinaryName() {
		String goplsBinary = "gopls";
		String os = System.getProperty("os.name");
		if (os.toLowerCase().startsWith("win")) {
			goplsBinary = "gopls.exe";
		}
		return goplsBinary;
	}

}