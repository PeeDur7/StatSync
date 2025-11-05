function NFLNavBar({selectedPosition, setSelectedPosition,
    filterText, setFilterText, loadPlayers,
    searchCategory, setSearchCategory}){
    const[filterDropdown,setFilterDropdown] = useState(false);
    const [teamListDropdown,setTeamListDropdown] = useState(false);
    const [teamList, setTeamList] = useState([]);

    useEffect(() =>{
        async function getTeamLogo(){
            try{
                const teamAPIUrl = await fetch("https://site.api.espn.com/apis/site/v2/sports/football/nfl/teams");
                const data = await teamAPIUrl.json();
                const teams = data.sports[0].leagues[0].teams;
                const teamMapList = teams.map(t => ({
                    name : t.team.abbreviation,
                    teamLogo : t.team.logos[0].href,
                    nameValue : t.team.displayName
                }));

                setTeamList(teamMapList);
            }catch(error){
            }
        }

        getTeamLogo();
    },[]);

    useEffect(() => {
        loadPlayers();
    },[selectedPosition,searchCategory]);

    const changeFilterDropdown = () => {
        setFilterDropdown(prev => !prev);
        setTeamListDropdown(false);
    }

    const changeSelectedCategory = (e) => {
        setSelectedPosition(e.target.value);
    }

    const teamListDrop = () => {
        setTeamListDropdown(prev => !prev);
    }

    const favoriteButton = () => {
        setFilterText("Favorite");
        setFilterDropdown(false);
        setSearchCategory("Favorite");
    }

    return(
        <div className="NFLNavBar">
            <button 
                value="All" 
                onClick={changeSelectedCategory}
                className={selectedPosition === "All" ? "active" : ""}>
                All
            </button>
            <button 
                value="QB" 
                onClick={changeSelectedCategory}
                className={selectedPosition === "QB" ? "active" : ""}>
                QB
            </button>
            <button 
                value="RB" 
                onClick={changeSelectedCategory}
                className={selectedPosition === "RB" ? "active" : ""}>
                RB
            </button>
            <button 
                value="WR" 
                onClick={changeSelectedCategory}
                className={selectedPosition === "WR" ? "active" : ""}>
                WR
            </button>
            <button 
                value="TE" 
                onClick={changeSelectedCategory}
                className={selectedPosition === "TE" ? "active" : ""}>
                TE
            </button>
            <button 
                value="PK"
                onClick={changeSelectedCategory}
                className={selectedPosition === "PK" ? "active" : ""}>
                K
            </button>
            <div className="NFLNavBarTeamFilter">
                <button value="Filter" onClick={changeFilterDropdown}>{filterText}</button>
                {filterDropdown && 
                    <>
                        <div className="NFLNavBarTeamList">
                            {!teamListDropdown && 
                                <>
                                    <button value="Team" onClick={teamListDrop}>Teams</button>
                                    <button value="NFL Favorites" onClick={favoriteButton}>Your Favorites</button>
                                    <button value="Clear" onClick=
                                        {() =>{
                                            setFilterDropdown(false);
                                            setTeamListDropdown(false);
                                            setFilterText("Filter");
                                            setSearchCategory("Filter");
                                        }}>
                                        Clear Filter
                                    </button>
                                </>
                            }
                            {teamListDropdown && 
                                teamList.map((team,index) => (
                                    <button key={index} value={team.name} onClick=
                                        {() => {
                                            setFilterDropdown(false); 
                                            setTeamListDropdown(false); 
                                            setFilterText(`${team.name}`);
                                            setSearchCategory(`${team.nameValue}`);
                                        }}>
                                        <img src={team.teamLogo}/> {team.name}
                                    </button>
                                ))
                            }
                        </div>
                    </>
                }
            </div>
        </div>
    )
}

export default NFLNavBar