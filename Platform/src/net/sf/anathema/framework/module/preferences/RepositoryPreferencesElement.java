package net.sf.anathema.framework.module.preferences;

import net.disy.commons.swing.layout.grid.GridDialogLayoutData;
import net.disy.commons.swing.layout.grid.GridDialogLayoutDataFactory;
import net.disy.commons.swing.layout.grid.IDialogComponent;
import net.sf.anathema.framework.configuration.InitializationPreferences;
import net.sf.anathema.framework.presenter.DirectoryFileChooser;
import net.sf.anathema.framework.presenter.action.preferences.IPreferencesElement;
import net.sf.anathema.initialization.PreferenceElement;
import net.sf.anathema.initialization.repository.RepositoryLocationResolver;
import net.sf.anathema.lib.gui.action.SmartAction;
import net.sf.anathema.lib.gui.dialog.message.MessageDialogFactory;
import net.sf.anathema.lib.gui.gridlayout.IGridDialogPanel;
import net.sf.anathema.lib.message.Message;
import net.sf.anathema.lib.resources.IResources;
import net.sf.anathema.lib.util.Identificate;
import net.sf.anathema.lib.util.Identified;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import static net.sf.anathema.framework.presenter.action.preferences.IAnathemaPreferencesConstants.REPOSITORY_PREFERENCE;

@PreferenceElement
public class RepositoryPreferencesElement implements IPreferencesElement {

  private File repositoryDirectory;
  private File defaultDirectory;
  private RepositoryLocationResolver repository;
  private boolean dirty;
  private boolean modificationAllowed = false;
  private JTextField repositoryTextField;
  private IResources resources;


  public RepositoryPreferencesElement() {
    try {
      repository = new RepositoryLocationResolver(InitializationPreferences.getDefaultPreferences());
      repositoryDirectory = new File(repository.resolve()).getCanonicalFile();
      defaultDirectory = new File(repository.getDefaultLocation()).getCanonicalFile();

      verifyDirectoriesExist();
    } catch (IOException e) {
      Throwable cause = e.getCause();
      MessageDialogFactory.showMessageDialog(null,
              new Message("An error occured while setting up the repository paths: " + cause.getMessage(),
                      cause)); //$NON-NLS-1$
    }
  }

  private void verifyDirectoriesExist() {
    try {
      if (!isValid() || !isDefaultValid()) {
        throw new IOException(
                "Unable to read/write/create user selected repository folder and default repository folder");
      }
    } catch (IOException e) {
      Throwable cause = e.getCause();
      MessageDialogFactory.showMessageDialog(null,
              new Message("An error occured while setting up the repository paths: " + cause.getMessage(),
                      cause)); //$NON-NLS-1$
    }
  }

  @Override
  public void addComponent(IGridDialogPanel panel, IResources resource) {
    verifyDirectoriesExist();

    this.resources = resource;
    panel.add(getComponent());
  }

  private IDialogComponent getComponent() {
    final JLabel repositoryLabel = new JLabel(resources.getString(
            "AnathemaCore.Tools.Preferences.RepositoryDirectory.Label") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
    repositoryTextField = new JTextField(45);
    repositoryTextField.setEditable(false);
    setDisplayedPath(repositoryDirectory);
    final JButton browseButton = createBrowseButton();
    final JButton defaultButton = createDefaultButton();
    modificationAllowed = true;
    return new IDialogComponent() {
      @Override
      public void fillInto(JPanel panel, int columnCount) {
        panel.add(repositoryLabel, GridDialogLayoutDataFactory.createHorizontalSpanData(1));
        panel.add(browseButton, GridDialogLayoutDataFactory.createRightData());
        panel.add(repositoryTextField, GridDialogLayoutDataFactory.createHorizontalSpanData(columnCount,
                GridDialogLayoutData.FILL_HORIZONTAL));
        panel.add(defaultButton, GridDialogLayoutDataFactory.createHorizontalSpanData(1));
        if (Desktop.isDesktopSupported()) {
          JButton openButton = createOpenButton();
          panel.add(openButton, GridDialogLayoutDataFactory.createRightData());
        }
      }

      @Override
      public int getColumnCount() {
        return 2;
      }
    };
  }

  private void setDisplayedPath(File selectedDirectory) {
    try {
      repositoryTextField.setText(selectedDirectory.getCanonicalPath());
    } catch (IOException e) {
      Throwable cause = e.getCause();
      MessageDialogFactory.showMessageDialog(null,
              new Message("An error occured while setting the repository path: " + cause.getMessage(),
                      cause)); //$NON-NLS-1$
    }
  }

  private JButton createBrowseButton() {
    return new JButton(new SmartAction(
            resources.getString("AnathemaCore.Tools.Preferences.RepositoryDirectory.ChooseDirectory")) { //$NON-NLS-1$

      @Override
      protected void execute(Component parent) {
        try {
          File selectedDir = DirectoryFileChooser.createDirectoryChooser(repositoryDirectory.getCanonicalPath(),
                  resources.getString("AnathemaCore.Tools.Preferences.RepositoryDirectory.ChooseDirectory"));
          if (selectedDir != null) {
            setDisplayedPath(selectedDir);
            repositoryDirectory = selectedDir;
            dirty = modificationAllowed;
          }
        } catch (IOException e) {
          Throwable cause = e.getCause();
          MessageDialogFactory.showMessageDialog(null,
                  new Message("An error occured while opening the directory browser: " + cause.getMessage(),
                          cause)); //$NON-NLS-1$
        }
      }
    });
  }

  private JButton createDefaultButton() {
    return new JButton(new SmartAction(
            resources.getString("AnathemaCore.Tools.Preferences.RepositoryDirectory.DefaultDirectory")) {

      @Override
      protected void execute(Component parent) {
        try {
          repositoryDirectory = new File(defaultDirectory.getCanonicalPath());
          setDisplayedPath(defaultDirectory);
          dirty = modificationAllowed;
        } catch (IOException e) {
          Throwable cause = e.getCause();
          MessageDialogFactory.showMessageDialog(null,
                  new Message("An error occured while setting the default path: " + cause.getMessage(),
                          cause)); //$NON-NLS-1$
        }

      }
    });
  }

  private JButton createOpenButton() {
    return new JButton(
            new SmartAction(resources.getString("AnathemaCore.Tools.Preferences.RepositoryDirectory.OpenDirectory")) {

              @Override
              protected void execute(Component parent) {
                try {
                  if (repositoryDirectory.exists()) {
                    Desktop.getDesktop().open(repositoryDirectory);
                  } else if (repositoryDirectory.getParentFile().exists()) {
                    Desktop.getDesktop().open(repositoryDirectory.getParentFile());
                  } else {
                    throw new IOException(
                            "Repository directory (and parent directory) deleted by user while the preferences dialog was open");
                  }
                } catch (IOException e) {
                  Throwable cause = e.getCause();
                  MessageDialogFactory.showMessageDialog(null,
                          new Message("An error occured while opening the repository path: " + cause.getMessage(),
                                  cause)); //$NON-NLS-1$
                }
              }
            });
  }

  @Override
  public void savePreferences() {
    try {
      if (repositoryDirectory.getCanonicalFile().equals(defaultDirectory.getCanonicalFile())) {
        SYSTEM_PREFERENCES.remove(REPOSITORY_PREFERENCE);
      } else {
        SYSTEM_PREFERENCES.put(REPOSITORY_PREFERENCE, repositoryDirectory.getCanonicalPath());
      }
    } catch (IOException e) {
      Throwable cause = e.getCause();
      if (cause == null) {
        cause = e;
      }
      MessageDialogFactory.showMessageDialog(null,
              new Message("An error occured while saving the repository preferences: " + cause.getMessage(),
                      cause)); //$NON-NLS-1$
    }
  }

  @Override
  public boolean isValid() {
    return new RepositoryFolderWorker().isValid(repositoryDirectory);
  }

  public boolean isDefaultValid() {
    return new RepositoryFolderWorker().isValid(defaultDirectory);
  }

  @Override
  public boolean isDirty() {
    return dirty;
  }

  @Override
  public Identified getCategory() {
    return new Identificate("Repository");
  }

  @Override
  public void reset() {
    repositoryDirectory = new File(repository.resolve());
    setDisplayedPath(repositoryDirectory);
    dirty = false;
  }
}