import React, {
  useState,
  useEffect,
  useRef,
  useCallback,
  useMemo,
} from "react";
import location from "../../assets/location.png";

// API 응답 포맷
interface PlaceLocation {
  latitude: number;
  longitude: number;
}

interface Place {
  id: string;
  formattedAddress: string;
  location: PlaceLocation;
  rating: number;
  userRatingCount: number;
  googleMapsUri?: string | null;
  displayName?: {
    text: string | null;
    languageCode?: string | null;
  } | null;
  primaryType?: string | null;
  editorialSummary?: {
    text: string;
    languageCode: string;
  } | null;
  goodForChildren?: boolean | null;
  paymentOptions?: {
    acceptsCreditCards?: boolean | null;
    acceptsCashOnly?: boolean | null;
    acceptsDebitCards?: boolean | null;
  } | null;
  priceLevel?: string | number | null;
}

interface Route {
  tourism: Place[];
  restaurant: Place[];
}

interface ResponseData {
  hotel: Place;
  routes: {
    [key: string]: Route;
  };
}

// 데이터 전처리 포맷
interface Location {
  lat: number;
  lng: number;
}

interface RouteData {
  tourism: Location[];
  restaurant: Location[];
}

interface TravelData {
  hotel: Location;
  routes: { [key: string]: RouteData };
}

// 맵을 위한 data - 하루 날짜에 대한 route만!
interface MapData {
  hotel: Location;
  route: RouteData;
}

// 요청 포맷
type RequestData = {
  location: {
    country: string;
    city: string;
  };
  days: number;
  transportation: string;
  preferences: {
    interest: string[];
    priority: number[];
    preferLoging: string;
  };
  attractions?: string[] | null;
};

// 데이터 전처리 함수 타입 정의
const parseTravelData = (data: ResponseData): TravelData => {
  const result: TravelData = {
    hotel: { lat: 0, lng: 0 }, // 초기화 값 설정
    routes: {
      "1": {
        tourism: [],
        restaurant: [],
      },
    },
  };

  // 호텔 위치 추출
  if (data.hotel && data.hotel.location) {
    const hotelLocation = data.hotel.location;
    result.hotel = {
      lat: hotelLocation.latitude,
      lng: hotelLocation.longitude,
    };
  }

  // 경로 정보 추출
  if (data.routes) {
    result.routes = {};
    Object.entries(data.routes).forEach(([day, routeInfo]) => {
      result.routes[day] = {
        tourism: [],
        restaurant: [],
      };
      Object.entries(routeInfo).forEach(([category, locationInfo]) => {
        const cate = category as keyof RouteData;
        result.routes[day][cate] = locationInfo.map((loc: Place) => ({
          lat: loc.location.latitude,
          lng: loc.location.longitude,
        }));
      });
    });
  }
  return result; // 모든 경우 반환값이 설정됨
};

// 추천 경로 API 요청
const receiveLocationData = async (
  requestData: RequestData,
): Promise<ResponseData | null> => {
  try {
    const response = await fetch(
      "http://localhost:8082/api/chatbot/recommendLocation",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(requestData),
      },
    );

    if (response.ok) {
      const data = await response.json();
      console.log("추천 결과:", data);
      return data as ResponseData;
    }

    console.error("Error:", response.statusText);
    return null; // void 반환
  } catch (error) {
    console.error("Error:", error);
    return null;
  }
};

const TravelMap: React.FC = () => {
  const [travelRoutes, setTravelRoutes] = useState<TravelData | null>(null);
  const [isFillAnswer, setIsFillAnswer] = useState<boolean>(false);
  const [selectedDay, setSelectedDay] = useState<string>("1");
  const [isMapLoaded, setIsMapLoaded] = useState<boolean>(false); // Google Maps 로드 상태
  const markersRef = useRef<google.maps.marker.AdvancedMarkerElement[]>([]);
  const pathLineRef = useRef<google.maps.Polyline | null>(null);
  const [map, setMap] = useState<google.maps.Map | null>(null); // Map 객체를 저장할 상태
  const mapRef = useRef<HTMLDivElement | null>(null); // 지도 DOM 요소를 참조할 ref

  const pastel = useMemo(
    () => [
      "#FFADAD",
      "#FFD6A5",
      "#FDFFB6",
      "#CAFFBF",
      "#A0C4FF",
      "#BDB2FF",
      "#FFC6FF",
    ],
    [],
  );

  // Google Maps API 스크립트를 동적으로 로드하고 로드가 완료되면 Promise를 해결
  const loadGoogleMaps = (): Promise<void> =>
    new Promise<void>((resolve) => {
      const existingScript = document.querySelector(
        `script[src*="maps.googleapis.com"]`,
      );
      if (existingScript) {
        resolve(); // 이미 로드된 경우 바로 resolve
        return;
      }

      const script = document.createElement("script");
      script.src =
        "https://maps.googleapis.com/maps/api/js?key=AIzaSyCR18GnRjPNu7vdaLL1Jvokinb1dqiMFTg&libraries=marker";
      script.async = true;
      script.onload = () => {
        setIsMapLoaded(true); // Google Maps가 로드된 후 상태 업데이트
        resolve();
      };
      document.head.appendChild(script);
    });

  // 지도 초기화 함수
  const initializeMap = useCallback(async () => {
    await loadGoogleMaps();
    if (window.google && mapRef.current) {
      const initialMap = new window.google.maps.Map(mapRef.current, {
        zoom: 15,
        disableDefaultUI: true,
        mapId: "225c22b94eba2c7f", // 생성된 Map ID 사용
      });
      setMap(initialMap);
    }
  }, []); // 빈 배열을 의존성 배열로 설정하여 함수 참조 고정

  useEffect(() => {
    // Google Maps 스크립트가 이미 로드된 경우, 즉시 상태를 true로 설정
    const existingScript = document.querySelector(
      `script[src*="maps.googleapis.com"]`,
    );
    if (existingScript) {
      console.log("Google Maps 스크립트가 이미 로드되었습니다.");
      setIsMapLoaded(true);
      return;
    }

    // 스크립트가 로드되지 않은 경우, 로드 함수 호출
    loadGoogleMaps().catch((error) =>
      console.error("Google Maps 로드 중 오류 발생:", error),
    );
  }, []); // 컴포넌트가 처음 마운트될 때만 실행

  // 로드 완료 시 initializeMap 호출
  useEffect(() => {
    console.log("isMapLoaded :", isMapLoaded);
    if (isMapLoaded) {
      console.log("initializeMap 호출 시작");
      initializeMap();
    }
  }, [isMapLoaded, initializeMap]); // isMapLoaded가 true가 되었을 때만 실행

  // 맵 초기화
  useEffect(() => {
    if (isMapLoaded) {
      if (mapRef.current && !map) {
        // 맵이 한 번만 초기화되도록 설정
        const newMap = new google.maps.Map(mapRef.current, {
          zoom: 15,
        });
        setMap(newMap); // 상태에 map 객체 저장
      }
    }
  }, [map, isMapLoaded]);

  // Marker Data 가져오기
  const getMarkerData = useCallback(
    async (coord: Location, index: string, color: string) => {
      try {
        if (!map) {
          console.error("Map 객체가 아직 초기화되지 않았습니다.");
          return null;
        }

        console.log(color);
        console.log(location);

        // 1. 마커 아이콘을 생성할 HTML 요소 생성
        // const iconElement = document.createElement("img");
        const iconElement = document.createElement("div");
        iconElement.style.width = "30px"; // 마커의 크기 설정
        iconElement.style.height = "30px";
        iconElement.style.position = "relative";
        iconElement.style.borderRadius = "50%"; // 동그란 마커
        iconElement.style.backgroundColor = color; // 아이콘의 색상 설정

        // 2. 라벨 요소 생성
        const labelElement = document.createElement("span");
        labelElement.style.position = "absolute";
        labelElement.style.top = "50%"; // 라벨을 중앙에 배치
        labelElement.style.left = "50%";
        labelElement.style.transform = "translate(-50%, -50%)"; // 중앙 정렬
        labelElement.style.color = "black";
        labelElement.style.fontSize = "12px";
        labelElement.style.fontWeight = "bold";
        labelElement.innerText = index; // 라벨의 텍스트 설정
        const pos = new google.maps.LatLng(coord.lat, coord.lng);

        // 3. 아이콘 요소에 라벨 추가
        iconElement.appendChild(labelElement);
        const marker = new google.maps.marker.AdvancedMarkerElement({
          position: pos,
          map,
          content: iconElement,
        });

        return marker;
      } catch (error) {
        console.error("Google Maps 라이브러리 로드 실패:", error);
        return null;
      }
    },
    [map],
  );

  // 날짜에 따라 맵 화면 갱신
  const initMap = useCallback(
    async (coordinates: MapData) => {
      console.log("초기화 : ", coordinates);

      // 기존 마커 삭제
      markersRef.current.forEach((marker) => {
        if (marker.content && marker.content.parentNode) {
          marker.content.parentNode.removeChild(marker.content);
        }
      });
      markersRef.current = []; // 마커 배열 초기화

      // 기존 라인 삭제
      if (pathLineRef.current) {
        pathLineRef.current.setMap(null);
      }

      // 새로운 마커와 라인 데이터를 초기화
      const newMarkers: google.maps.marker.AdvancedMarkerElement[] = [];
      const path: google.maps.LatLng[] = [];
      const hotelLocation = coordinates.hotel;
      const bounds = new window.google.maps.LatLngBounds();
      const hotelLatLng = new google.maps.LatLng(
        hotelLocation.lat,
        hotelLocation.lng,
      );

      // 호텔 위치 마커 추가
      let ind = 0;
      await getMarkerData(hotelLocation, "❤️", pastel[0]).then(
        (startMarker) => {
          if (startMarker) {
            newMarkers.push(startMarker);
          }
        },
      );
      path.push(hotelLatLng);
      bounds.extend(hotelLatLng);

      const locationPromises = Object.values(coordinates.route).flatMap(
        (locations) =>
          locations.map(async (loc: Location) => {
            const latlng = new google.maps.LatLng(loc.lat, loc.lng);
            ind += 1;
            const marker = await getMarkerData(
              loc,
              String(ind),
              pastel[ind % pastel.length],
            );
            if (marker) {
              marker.map = map;
              newMarkers.push(marker);
              path.push(latlng);
              bounds.extend(latlng);
            }
            return marker;
          }),
      );

      await Promise.all(locationPromises);

      const lineSymbol = {
        path: "M 0,-1 0,1",
        strokeOpacity: 1,
        scale: 4,
      };

      const newPathLine = new google.maps.Polyline({
        path,
        geodesic: true,
        strokeColor: "#000000",
        strokeOpacity: 0,
        strokeWeight: 30,
        icons: [
          {
            icon: lineSymbol,
            offset: "0",
            repeat: "20px",
          },
        ],
      });

      if (map) {
        newPathLine.setMap(map);
        map.fitBounds(bounds);
      }

      // useRef로 관리된 값을 업데이트
      markersRef.current = newMarkers;
      pathLineRef.current = newPathLine;
    },
    [map, pastel, getMarkerData],
  );

  useEffect(() => {
    if (isMapLoaded) {
      initializeMap();
    }
  }, [isMapLoaded, initializeMap]);

  // 답변 완료 하였을 때 travel route받아오기
  useEffect(() => {
    if (isFillAnswer) {
      // 비동기 작업을 위한 함수 정의
      const fetchData = async () => {
        const requestData = {
          location: {
            country: "한국",
            city: "서울",
          },
          days: 7,
          transportation: "도보",
          preferences: {
            interest: ["아이와 함께", "자연 속 힐링"],
            priority: [1, 2, 4, 3],
            preferLoging: "호텔",
          },
          attractions: ["ChIJod7tSseifDUR9hXHLFNGMIs"],
        };

        try {
          const travelData = await receiveLocationData(requestData);
          if (travelData) {
            const parsedData = parseTravelData(travelData);
            console.log("파싱 : ", parsedData);
            setTravelRoutes(parsedData);
          }
        } catch (error) {
          console.error("데이터를 가져오는 중 오류 발생:", error);
        }
      };

      // 정의한 비동기 함수 호출
      fetchData();
    }
  }, [isFillAnswer]);

  // selectedDay에 맞게 Map 초기화 하기
  useEffect(() => {
    if (!travelRoutes) return;
    const mapData: Partial<MapData> = {};
    mapData.hotel = travelRoutes.hotel;
    mapData.route = travelRoutes.routes[selectedDay];
    console.log("map : ", mapData);

    if (mapData.hotel && mapData.route) {
      initMap(mapData as MapData);
    }
  }, [selectedDay, travelRoutes, initMap]);

  return (
    <div>
      <button type="button" onClick={() => setIsFillAnswer(true)}>
        답변 확인
      </button>
      <h3>부산 여행 경로</h3>
      <div>
        {travelRoutes &&
          Object.keys(travelRoutes.routes).map((day) => (
            <button type="button" key={day} onClick={() => setSelectedDay(day)}>
              {day}일차
            </button>
          ))}
      </div>
      <div ref={mapRef} style={{ height: "500px", width: "100%" }}>
        {" "}
      </div>
    </div>
  );
};

export default TravelMap;
