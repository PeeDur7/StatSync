import { useEffect, useState } from "react"

function NBANavBar({selectedPosition,setSelectedPosition,
    filterText,setFilterText, loadPlayers,
    searchCategory, setSearchCategory}){

    const [teamList, setTeamList] = useState([]);
    const[filterDropdown,setFilterDropdown] = useState(false);
    const [teamListDropdown,setTeamListDropdown] = useState(false);
    
    
    useEffect(() => {
        async function getTeamInfo(){
            try{
                const teams = [];
                for(let i = 1; i<=30; i++){
                    const url = `https://site.api.espn.com/apis/site/v2/sports/basketball/nba/teams/${i}`;
                    const response = await fetch(url);
                    const data = await response.json();
                    const team = {
                        name: data.team.abbreviation,
                        teamLogo: data.team.logos[0].href,
                        nameValue: data.team.displayName
                    }
                    teams.push(team);
                }
                setTeamList(teams);
            } catch(e){
            }
        }

        getTeamInfo();
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
        <div className="NBANavBar">
            <button value="All" onClick={changeSelectedCategory}>All</button>
            <button value="G" onClick={changeSelectedCategory}>G</button>
            <button value="F" onClick={changeSelectedCategory}>F</button>
            <button value="C" onClick={changeSelectedCategory}>C</button>
            <div className="NBANavBarTeamFilter">
                <button value="Filter" onClick={changeFilterDropdown}>{filterText}</button>
                {filterDropdown && 
                    <>
                        <div className="NBANavBarTeamList">
                            {!teamListDropdown && 
                                <>
                                    <button value="Team" onClick={teamListDrop}>Teams</button>
                                    <button value="NBA Favorites" onClick={favoriteButton}>Your Favorites</button>
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
                                        <img src={team.teamLogo} alt={team.name}/> {team.name}
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

export default NBANavBar