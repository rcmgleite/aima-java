package aima.gui.applications.search.map;

import java.util.ArrayList;

import aima.core.environment.map.AdaptableHeuristicFunction;
import aima.core.environment.map.ExtendableMap;
import aima.core.environment.map.GenericRoadMap;
import aima.core.environment.map.MapAgent;
import aima.core.environment.map.MapEnvironment;
import aima.core.environment.map.Scenario;
import aima.core.util.datastructure.Point2D;
import aima.gui.framework.AgentAppController;
import aima.gui.framework.AgentAppEnvironmentView;
import aima.gui.framework.AgentAppFrame;
import aima.gui.framework.MessageLogger;
import aima.gui.framework.SimpleAgentApp;

/**
 * Demo example of a route finding agent application with GUI. The main method
 * starts a map agent frame and supports runtime experiments. This
 * implementation is based on the {@link aima.core.environment.map.MapAgent} and
 * the {@link aima.core.environment.map.MapEnvironment}. It can be used as a
 * code template for creating new applications with different specialized kinds
 * of agents and environments.
 * 
 * @author Ruediger Lunde
 */
public class RouteFindingAgentApp extends SimpleAgentApp {

	/** Creates a <code>MapAgentView</code>. */
	public AgentAppEnvironmentView createEnvironmentView() {
		return new ExtendedMapAgentView();
	}
	
	/** Creates and configures a <code>RouteFindingAgentFrame</code>. */
	@Override
	public AgentAppFrame createFrame() {
		return new RouteFindingAgentFrame();
	}

	/** Creates a <code>RouteFindingAgentController</code>. */
	@Override
	public AgentAppController createController() {
		return new RouteFindingAgentController();
	}
	
	private static String usedMap = "";

	// //////////////////////////////////////////////////////////
	// local classes

	/** Frame for a graphical route finding agent application. */
	protected static class RouteFindingAgentFrame extends MapAgentFrame {
		private static final long serialVersionUID = 1L;

		/** Creates a new frame. */
		public RouteFindingAgentFrame() {
			setTitle("RFA - the Route Finding Agent");
			setSelectorItems(MAP_SELECTION, Utils.listMapFiles(null), 0);
			setSelectorItems(SEARCH_MODE_SEL, SearchFactory.getInstance()
					.getSearchModeNames(), 1); // change the default!
			setSelectorItems(HEURISTIC_SEL, new String[] { "=0", "SLD" }, 1);
		}

		/**
		 * Changes the destination selector items depending on the scenario
		 * selection if necessary, and calls the super class implementation
		 * afterwards.
		 */
		@Override
		protected void selectionChanged(String changedSelector) {
			String mapName = null;
			if(changedSelector == null) {
				return;
			}else {
				switch (changedSelector) {
				case MAP_SELECTION:
					SelectionState state = getSelection();
					int mapIndex = state.getIndex(MapAgentFrame.MAP_SELECTION);
					mapName = Utils.listMapFiles(null)[mapIndex];
					System.out.println("[INFO] " + mapName);
					if(!mapName.equals(usedMap)){
						GenericRoadMap instance = GenericRoadMap.getInstance(mapName);
						if (instance == null) {
							System.out.println("[ERROR] Fatal error ocurred. map instance is null");
							return;
						}
						ArrayList<String> destPossibilities = instance.getDsts();
						ArrayList<String> srcPossibilities = instance.getSrcs();
						setSelectorItems(DESTINATION_SEL, destPossibilities.toArray(), 0);
						setSelectorItems(SOURCE_SEL, srcPossibilities.toArray(), 0);
					}
					usedMap = mapName;
					break;
				default:
					break;
				}
			}
			
			super.selectionChanged(mapName);
		}
	}

	/** Controller for a graphical route finding agent application. */
	protected static class RouteFindingAgentController extends
			AbstractMapAgentController {
		/**
		 * Configures a scenario and a list of destinations. Note that for route
		 * finding problems, the size of the list needs to be 1.
		 */
		@Override
		protected void selectScenarioAndDest(String mapFilepath, int scenarioIdx, int destIdx) {
			ExtendableMap map = new ExtendableMap();
			
			String agentLoc = null;
			
			if(mapFilepath == null || mapFilepath.equals("")) {
				System.out.println("[INFO] Using map: " + usedMap);
				mapFilepath = usedMap;
			}
			
			GenericRoadMap m = GenericRoadMap.getInstance(mapFilepath, map);
            agentLoc = m.getSrcs().get(scenarioIdx);
            
            MapEnvironment env = new MapEnvironment(m);
            scenario = new Scenario(env, m, agentLoc);
            destinations = new ArrayList<String>();
            destinations.add(m.getDsts().get(destIdx));
  		}

		/**
		 * Prepares the view for the previously specified scenario and
		 * destinations.
		 */
		@Override
		protected void prepareView() {
			ExtendedMapAgentView mEnv = (ExtendedMapAgentView) frame.getEnvView();
			mEnv.setData(scenario, destinations, null);
			mEnv.setEnvironment(scenario.getEnv());
		}

		/**
		 * Returns the trivial zero function or a simple heuristic which is
		 * based on straight-line distance computation.
		 */
		@Override
		protected AdaptableHeuristicFunction createHeuristic(int heuIdx) {
			AdaptableHeuristicFunction ahf = null;
			switch (heuIdx) {
			case 0:
				ahf = new H1();
				break;
			default:
				ahf = new H2();
			}
			return ahf.adaptToGoal(destinations.get(0), scenario
					.getAgentMap());
		}

		/**
		 * Creates a new agent and adds it to the scenario's environment.
		 */
		@Override
		public void initAgents(MessageLogger logger) {
			if (destinations.size() != 1) {
				logger.log("Error: This agent requires exact one destination.");
				return;
			}
			MapEnvironment env = scenario.getEnv();
			String goal = destinations.get(0);
			MapAgent agent = new MapAgent(env.getMap(), env, search, new String[] { goal });
			env.addAgent(agent, scenario.getInitAgentLocation());
		}
	}

	/**
	 * Returns always the heuristic value 0.
	 */
	static class H1 extends AdaptableHeuristicFunction {

		public double h(Object state) {
			return 0.0;
		}
	}

	/**
	 * A simple heuristic which interprets <code>state</code> and {@link #goal}
	 * as location names and uses the straight-line distance between them as
	 * heuristic value.
	 */
	static class H2 extends AdaptableHeuristicFunction {

		public double h(Object state) {
			double result = 0.0;
			Point2D pt1 = map.getPosition((String) state);
			Point2D pt2 = map.getPosition((String) goal);
			if (pt1 != null && pt2 != null)
				result = pt1.distance(pt2);
			return result;
		}
	}

	// //////////////////////////////////////////////////////////
	// starter method

	/** Application starter. */
	public static void main(String args[]) {
		new RouteFindingAgentApp().startApplication();
	}
}
