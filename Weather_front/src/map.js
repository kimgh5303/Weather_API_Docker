// map.js

import React, { useEffect, useState } from 'react';
import { GoogleMap, LoadScript, Marker } from '@react-google-maps/api';
import axios from 'axios';
import './box.css';
import { serverIP } from "./config";

const MapContainer = () => {
    const [currentPosition, setCurrentPosition] = useState(null);
    const [locations, setLocations] = useState([]);
    const [newLocation, setNewLocation] = useState({ id: '', name: '', lat: 0, lng: 0 });
    const [selectedLocation, setSelectedLocation] = useState(null);

    useEffect(() => {
        navigator.geolocation.getCurrentPosition(
        (position) => {
            const { latitude, longitude } = position.coords;
            setCurrentPosition({ lat: latitude, lng: longitude });
        },
        (error) => {
            console.error('Error getting user location:', error);
        }
        );

        fetchLocations();
        
        // ESC 키를 눌렀을 때 모달이 닫히도록 이벤트 리스너 추가
        const handleKeyPress = (e) => {
            if (e.key === 'Escape') {
                closeModal();
            }
        };

        // 이벤트 리스너 등록
        window.addEventListener('keydown', handleKeyPress);

        // 컴포넌트가 언마운트될 때 이벤트 리스너 제거
        return () => {
            window.removeEventListener('keydown', handleKeyPress);
        };
    }, []);

    const closeModal = () => {
        setSelectedLocation(null);
    };

    const fetchLocations = async () => {
        try {
        // const response = await axios.get(`${serverIP}/weathers/locations`);
        const response = await axios.get('/weathers/locations');
        setLocations(response.data.data || []);
        } catch (error) {
        console.error('Error fetching locations:', error);
        }
    };

    const addLocation = async () => {
        try {
            const formData = new URLSearchParams();
            formData.append('address', newLocation.name);

            await axios.post('/weathers/locations', formData, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded', // x-www-form-urlencoded 형식으로 전송함을 명시
                },
            });

            fetchLocations();
            setNewLocation({ name: '' });  // 주소 값을 전송했으므로 나머지 필드는 초기화
        } catch (error) {
            console.error('Error adding location:', error);
        }
    };

    const updateLocation = async (id, address) => {
        try {
            // const formData = new URLSearchParams();
            // formData.append('id', newLocation.id);
            // formData.append('address', newLocation.name);
            // console.log(formData.toString())
            await axios.put('/weathers/locations', {id, address}, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded', // x-www-form-urlencoded 형식으로 전송함을 명시
            },
            });
            fetchLocations();
        } catch (error) {
        console.error('Error updating location:', error);
        }
    };

    const deleteLocation = async (locationId) => {
        try {
        await axios.delete(`/weathers/locations/${locationId}`);
        fetchLocations();
        } catch (error) {
        console.error('Error deleting location:', error);
        }
    };

    const handleLocationClick = (location) => {
        setSelectedLocation(location);
    };

    return (
        <div style={{ display: 'flex', width: '100%' }}>
            <LoadScript googleMapsApiKey="AIzaSyAruD8tKBw4IEUms327Muaq5clYvn10YxA">
                <GoogleMap
                mapContainerStyle={{ height: '800px', width: '50%' }}
                center={currentPosition || { lat: 0, lng: 0 }}
                zoom={10}
                >
                {currentPosition && <Marker position={currentPosition} />}
                {locations.map((location) => (
                    <Marker
                        key={location.locIdx}
                        position={{ lat: parseFloat(location.locLatitude), lng: parseFloat(location.locLongitude) }}
                        onClick={() => handleLocationClick(location)}
                    />
                ))}
                </GoogleMap>
            </LoadScript>

            <div style={{ width: '20%', padding: '20px', backgroundColor: '#f0f0f0', display: 'flex', flexDirection: 'column' }}>
                <h2 style={{ marginBottom: '10px' }}>관심 지역</h2>
                <div style={{ marginBottom: '10px', display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
                <input
                    type="text"
                    placeholder="Location Name"
                    value={newLocation.name}
                    onChange={(e) => setNewLocation({ ...newLocation, name: e.target.value })}
                    style={{ padding: '5px', marginRight: '10px' }}
                />
                {/* Add Location 버튼 */}
                <button
                onClick={() => addLocation(true)}
                style={{ backgroundColor: '#4CAF50', color: 'white', padding: '10px', border: 'none', cursor: 'pointer', marginTop: '10px' }}
                >
                Add Location
                </button>
                </div>

                <ul className="location-list" style={{ marginTop: '10px', listStyleType: 'none', padding: '0' }}>
                    {locations.length === 0 ? (
                        <li className="no-interest-box">No interest locations available.</li>
                    ) : (
                        locations.map((location) => (
                        <li key={location.locIdx} className="location-item" style={{ marginBottom: '5px' }}>
                            <div className="location-container">
                                <div className="location-box" onClick={() => handleLocationClick(location)}>
                                    <span className="location-name">{location.locName}</span>
                                </div>
                                <div className="button-group">
                                    <button
                                        className="update-button"
                                        onClick={() => updateLocation(location.locId, prompt('Enter new name:', location.locName) )}
                                    >
                                        Update
                                    </button>
                                    <button className="delete-button" onClick={() => deleteLocation(location.locId)}>
                                        Delete
                                    </button>
                                </div>
                            </div>
                            {location.popup && (
                            <div className="popup">
                                {/* 여기에 특정 정보 표시할 내용 추가 */}
                                <p>특정 정보: {location.specialInfo || '없음'}</p>
                            </div>
                            )}
                        </li>
                        ))
                    )}
                </ul>
            </div>
            <div style={{ width: '30%', padding: '20px', backgroundColor: '#f0f0f0', display: 'flex', flexDirection: 'column' }}>
                <img src="/img/panda.jpg" alt="img" style={{ width: '100%', height: '50%' }} />
                <div style={{ display: 'flex' }}>
                    <img src="/img/panda2.jpeg" alt="img" style={{ width: '50%'}} />
                    <img src="/img/image.jpg" alt="img" style={{ width: '50%'}} />
                </div>
            </div>


            {selectedLocation && (
                <div className="modal" style={{ position: 'fixed', top: 0, left: 0, width: '100%', height: '100%', backgroundColor: 'rgba(0, 0, 0, 0.5)', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                <div style={{ backgroundColor: 'white', padding: '20px', borderRadius: '10px' }}>
                    <h3>Location Details</h3>
                    <p>장소: {selectedLocation.locName}</p>
                    <p>위도: {selectedLocation.locLatitude}</p>
                    <p>경도: {selectedLocation.locLongitude}</p>
                    <p>기온: {selectedLocation.temp}℃</p>
                    <p>풍향: {selectedLocation.windDir}°</p>
                    <p>풍속: {selectedLocation.windSpeed}m/s</p>
                    <p>습도: {selectedLocation.humidity}%</p>
                    <button onClick={closeModal}>Close</button>
                </div>
                </div>
            )}
        </div>
    );
};

export default MapContainer;
