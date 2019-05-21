// This file was generated by Mendix Modeler.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package databaseconnectortest.actions;

import com.mendix.core.Core;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import databaseconnectortest.tools.RedshiftClusterOperations;

public class CreateRedshiftCluster extends CustomJavaAction<java.lang.Boolean>
{
	public CreateRedshiftCluster(IContext context)
	{
		super(context);
	}

	@java.lang.Override
	public java.lang.Boolean executeAction() throws Exception
	{
		// BEGIN USER CODE
    new RedshiftClusterOperations(logNode).createCluster();

    return true;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "CreateRedshiftCluster";
	}

	// BEGIN EXTRA CODE
  private final ILogNode logNode = Core.getLogger(this.getClass().getName());
	// END EXTRA CODE
}
