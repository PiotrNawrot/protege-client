package org.protege.editor.owl.client.action;

import org.protege.editor.owl.client.api.exception.SynchronizationException;
import org.protege.editor.owl.client.util.ChangeUtils;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.owl.server.api.exception.OWLServerException;
import org.protege.owl.server.changes.api.VersionedOntologyDocument;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ShowStatusAction extends AbstractClientAction {

    private static final long serialVersionUID = 4601012273632698091L;

    private OWLModelManagerListener listener = new OWLModelManagerListener() {
        @Override
        public void handleChange(OWLModelManagerChangeEvent event) {
            updateEnabled();
        }
    };

    @Override
    public void initialise() throws Exception {
        super.initialise();
        getOWLModelManager().addListener(listener);
    }

    private void updateEnabled() {
        setEnabled(getOntologyResource().isPresent());
    }

    @Override
    public void dispose() throws Exception {
        super.dispose();
        getOWLModelManager().removeListener(listener);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        try {
            final VersionedOntologyDocument vont = getActiveVersionedOntology();

            JDialog dialog = new JDialog();
            dialog.setTitle("Client status");
            dialog.setLocationRelativeTo(getOWLWorkspace());

            JPanel panel = new JPanel(new GridLayout(0, 2));

            panel.add(new JLabel("Server Document:"));
            panel.add(new JLabel(vont.getRemoteFile().getName()));

            panel.add(new JLabel("Local Revision:"));
            panel.add(new JLabel(vont.getRevision().toString()));

            panel.add(new JLabel("Latest Server Revision:"));
            panel.add(new JLabel(ChangeUtils.getRemoteHeadRevision(vont).toString()));

            panel.add(new JLabel("# of uncommitted changes:"));
            panel.add(new JLabel(ChangeUtils.getUncommittedChanges(vont).size()+""));

            dialog.getContentPane().setLayout(new BorderLayout());
            dialog.getContentPane().add(panel, BorderLayout.CENTER);
            dialog.pack();
            dialog.setVisible(true);

        }
        catch (SynchronizationException e) {
            showSynchronizationErrorDialog(e.getMessage(), e);
        }
        catch (OWLServerException e) {
            showSynchronizationErrorDialog("Show change status failed: " + e.getMessage(), e);
        }
    }
}
