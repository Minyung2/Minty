import React, { useState, useEffect } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Pagination from './pagination';
import { Button, Container, Row, Col, Nav, Form, Modal, Dropdown , DropdownButton} from 'react-bootstrap';
import RangeSlider from 'react-bootstrap-range-slider';
import '../css/boardList.css';
import { formatDistanceToNow, parseISO } from 'date-fns';
import { ko } from 'date-fns/locale';
import { BiSearch } from 'react-icons/bi';
import InfiniteScroll from 'react-infinite-scroll-component';
import { Map, MapMarker } from 'react-kakao-maps-sdk';


function BoardList() {
  const [topCategories, setTopCategories] = useState([]);
  const [subCategories, setSubCategories] = useState([]);
  const [loadedBoardIds, setLoadedBoardIds] = useState([]); // 기존에 로드된 게시물의 ID 저장
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [tradeBoards, setTradeBoards] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchQueryInput, setSearchQueryInput] = useState('');
  const [subCategoryId, setSubCategoryId] = useState(null);
  const [minPrice, setMinPrice] = useState(null);
  const [maxPrice, setMaxPrice] = useState(null);
  const [minPriceInput, setMinPriceInput] = useState(null);
  const [maxPriceInput, setMaxPriceInput] = useState(null);
  const [sortBy, setSortBy] = useState('');
  const [activeFilters, setActiveFilters] = useState([]);
  const [page, setPage] = useState(pageParam ? Number(pageParam) : 0);
  const [hasMore, setHasMore] = useState(true);
  const { page: pageParam } = useParams();
    const [userLocationList, setUserLocationList] = useState([]);
    const [searchArea, setSearchArea] = useState('');
    const [showUserLocationModal,setShowUserLocationModal] = useState(false);
    const [selectedArea, setSelectedArea] = useState('');
    const [mapLevel, setMapLevel] = useState(50);

    const [map, setMap] = useState(null);
      const [lineOverlay, setLineOverlay] = useState(null);
      const [level, setLevel] = useState(0);

  const handleSearch = (e) => {
    e.preventDefault();
    const searchQuery = e.target.elements.searchQuery.value;
    setSearchQuery(searchQuery);
    setPage(0);
    if (searchQuery) {
      const existingFilter = activeFilters.find((filter) => filter.type === '검색어');
      if (existingFilter) {
        setActiveFilters((prevFilters) =>
          prevFilters.map((filter) =>
            filter.type === '검색어' ? { type: '검색어', value: searchQuery } : filter
          )
        );
      } else {
        setActiveFilters((prevFilters) => [...prevFilters, { type: '검색어', value: searchQuery }]);
      }
    }

    fetchData();
  };

     const handleAreaSearch = (address, dong) => {
       setSelectedArea(dong);
       setSearchArea(address);
       setPage(0);
     };


  const handleSortByChange = (e) => {
    const selectedSortBy = e.target.value;
    setSortBy(selectedSortBy);
    setPage(0);
    if (selectedSortBy) {
      let filterValue;
      switch (selectedSortBy) {
        case 'itemDesc':
          filterValue = '최신순';
          break;
        case 'priceAsc':
          filterValue = '낮은 가격순';
          break;
        case 'priceDesc':
          filterValue = '높은 가격순';
          break;
        default:
          filterValue = '';
          break;
      }

      const existingFilter = activeFilters.find((filter) => filter.type === '정렬 방식');
      if (existingFilter) {
        setActiveFilters((prevFilters) =>
          prevFilters.map((filter) =>
            filter.type === '정렬 방식' ? { type: '정렬 방식', value: filterValue } : filter
          )
        );
      } else {
        setActiveFilters((prevFilters) => [...prevFilters, { type: '정렬 방식', value: filterValue }]);
      }
    }

    fetchData();
  };

  const searchByPrice = (e) => {
    e.preventDefault();
    const minPrice = e.target.elements.minPrice.value;
    const maxPrice = e.target.elements.maxPrice.value;
    setMinPrice(parseInt(minPrice));
    setMaxPrice(parseInt(maxPrice));
    setPage(0);
    const existingMinPriceFilter = activeFilters.find((filter) => filter.type === '최소 가격');
    const existingMaxPriceFilter = activeFilters.find((filter) => filter.type === '최대 가격');

    if (minPrice && existingMinPriceFilter) {
      setActiveFilters((prevFilters) =>
        prevFilters.map((filter) =>
          filter.type === '최소 가격' ? { type: '최소 가격', value: minPrice } : filter
        )
      );
    } else if (minPrice) {
      setActiveFilters((prevFilters) => [...prevFilters, { type: '최소 가격', value: minPrice }]);
    }

    if (maxPrice && existingMaxPriceFilter) {
      setActiveFilters((prevFilters) =>
        prevFilters.map((filter) =>
          filter.type === '최대 가격' ? { type: '최대 가격', value: maxPrice } : filter
        )
      );
    } else if (maxPrice) {
      setActiveFilters((prevFilters) => [...prevFilters, { type: '최대 가격', value: maxPrice }]);
    }

    fetchData();
  };

    useEffect(() => {
           fetchData();
    }, [subCategoryId, searchQuery, minPrice, maxPrice, sortBy, searchArea]);

 const fetchData = async () => {
   let endpoint = '/api/boardList';
   if (searchArea) {
     endpoint += `/searchArea/${searchArea}`
   }
   if (subCategoryId) {
     endpoint += `/category/${subCategoryId}`;
   }
   if (searchQuery) {
     endpoint += `/searchQuery/${searchQuery}`;
   }
   if (minPrice) {
     endpoint += `/minPrice/${minPrice}`;
   }
   if (maxPrice) {
     endpoint += `/maxPrice/${maxPrice}`;
   }
   if (sortBy) {
     endpoint += `/sortBy/${sortBy}`;
   }

   endpoint += `/page/${page}`;

   console.log(endpoint);
   await axios
     .get(endpoint)
     .then((response) => {
       if (page === 0 && endpoint != `/api/boardList/page/0`) {
         // If it's the first page, reset the tradeBoards state
         setTradeBoards(response.data.tradeBoards);
       } else {
         // If it's not the first page, append the new tradeBoards to the existing state
         setTradeBoards((prevBoards) => [...prevBoards, ...response.data.tradeBoards]);
       }
       let top = [...response.data.top];
       let sub = [...response.data.sub];
       let locations = [...response.data.userLocationList];
       setTopCategories(top);
       setSubCategories(sub);
       if(!searchArea){
        setSearchArea(response.data.userLocationList[0].address);
       }
       setUserLocationList(locations);
       const nextPage = page + 1; // Calculate the next page
       setPage(nextPage); // Update the page state to the next page
       setHasMore(response.data.hasNext);
     })
     .catch((error) => {
       console.error('Error fetching data:', error);
     });
 };



const fetchDataWithDelay = () => {
  setTimeout(fetchData, 500);
};


  const removeFilter = (filterType) => {
    setActiveFilters((prevFilters) => prevFilters.filter((filter) => filter.type !== filterType));
    switch (filterType) {
      case '검색어':
        setSearchQuery('');
        setPage(0);
        break;
      case '최소 가격':
        setMinPrice(null);
        setPage(0);
        break;
      case '최대 가격':
        setMaxPrice(null);
        setPage(0);
        break;
      case '정렬 방식':
        setSortBy('');
        setPage(0);
        break;
      case '카테고리':
        setSubCategoryId(null);
        setPage(0);
        break;
      default:
        break;
    }

    fetchData();
  };


  const renderTopCategories = topCategories.map((category) => (
    <Nav.Item key={category.id}>
      <Button
        onClick={() => handleTopCategoryClick(category.id)}
        active={category.id === selectedCategory}
        className="category-link"
        style={{ backgroundColor: 'white' }}
        type="button"
      >
        {category.name}
      </Button>
    </Nav.Item>
  ));

  const renderSubCategories = subCategories
    .filter((subcategory) => subcategory.topCategory.id === selectedCategory)
    .map((subcategory) => (
      <Nav.Item key={subcategory.id}>
        <Button
          onClick={() => handleSubCategoryClick(subcategory.id, subcategory.name)}
          className={`sub-category-link ${subCategoryId === subcategory.id ? 'active' : ''}`}
        >
          {subcategory.name}
        </Button>
      </Nav.Item>
    ));
  const handleTopCategoryClick = (categoryId) => {
    setSelectedCategory(categoryId === selectedCategory ? null : categoryId);
    setSubCategoryId(null);
  };

  const handleSubCategoryClick = (subCategoryId, subCategoryName) => {
    setSubCategoryId(subCategoryId);
    setPage(0);
    const existingSubCategoryFilter = activeFilters.find((filter) => filter.type === '카테고리');
    if (existingSubCategoryFilter) {
      setActiveFilters((prevFilters) =>
        prevFilters.map((filter) =>
          filter.type === '카테고리' ? { type: '카테고리', value: subCategoryName } : filter
        )
      );
    } else {
      setActiveFilters((prevFilters) => [...prevFilters, { type: '카테고리', value: subCategoryName }]);
    }
  };

   const setShowUserLocationList = () => {
          onLoadKakaoMap();
          setShowUserLocationModal(true);
      }

      const handleUserLocationCloseModal = () => {
          setShowUserLocationModal(false);
      }

    const extractDong = (address) => {
      const addressParts = address.split(" ");
      const dong = addressParts[addressParts.length - 1];
      return dong;
    };

    const onLoadKakaoMap = () => {
    axios.get('/api/getMapData')
    .then((response) => {
      const script = document.createElement('script');
      script.async = true;
      script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${response.data.apiKey}&autoload=false&libraries=services`;
      document.head.appendChild(script);

      script.onload = () => {
        window.kakao.maps.load(() => {
          const mapContainer = document.getElementById('map');
          const mapOption = {
            center: new window.kakao.maps.LatLng(37.402054, 127.108209),
            level: 3
          };
          var map = new window.kakao.maps.Map(mapContainer, mapOption);
        });
      };
    });
  };






      useEffect(() => {
          // 레벨에 따라 선을 그리는 함수
          function drawLine(level) {
            if (map && lineOverlay) {
              lineOverlay.setMap(null); // 기존에 그려진 선 제거

              const center = map.getCenter();
              const radius = getRadius(level);

              const linePath = [
                // 선의 좌표 배열
                new window.kakao.maps.LatLng(center.getLat() - radius, center.getLng()),
                new window.kakao.maps.LatLng(center.getLat() + radius, center.getLng()),
              ];

              const line = new window.kakao.maps.Polyline({
                path: linePath,
                strokeWeight: 2,
                strokeColor: '#FF0000',
                strokeOpacity: 0.7,
              });

              line.setMap(map);
              setLineOverlay(line);
            }
          }

          drawLine(level);
        }, [map, level]);

         function getRadius(level) {
            switch (level) {
              case 0:
                return 3000; // 3km 반경
              case 1:
                return 5000; // 5km 반경
              case 2:
                return 10000; // 10km 반경
              default:
                throw new Error('Invalid level');
            }
          }

          const handleSliderChange = (e) => {
              setLevel(e.target.value);
            };


  return (
    <Container fluid>
      <Row className="justify-content-start filtertext">
        <div>
          <p>필터</p>
          {activeFilters.map((filter, index) => (
            <React.Fragment key={filter.type}>
              <div className={`filter ${filter.value.length > 10 ? 'long-text' : ''}`} key={filter.type}>
                <span>
                  {filter.type} : {filter.value}
                </span>
                <button onClick={() => removeFilter(filter.type)}>x</button>
              </div>
              {(index + 1) % 2 === 0 && <br />} {/* 줄바꿈을 위한 br 요소 */}
            </React.Fragment>
          ))}
        </div>
      </Row>
      <Row className="justify-content-start">
        <Col md={1}>
            <Dropdown className="dark-dropdown">
            <Dropdown.Toggle id="dropdown-button-dark" variant="secondary">
              {selectedArea ? selectedArea : userLocationList.length > 0 ? extractDong(userLocationList[0].address) : ''}
            </Dropdown.Toggle>

            <Dropdown.Menu>
              {userLocationList.map((loc) => {
                 const dong = extractDong(loc.address); // Extract the last part as the "동" information
                return (
                 <Dropdown.Item key={loc.address} onClick={() => handleAreaSearch(loc.address, dong)}>
                   {dong}
                 </Dropdown.Item>
                );
              })}
              <Dropdown.Divider style={{borderColor: "white"}} />
              <Dropdown.Item onClick={setShowUserLocationList}>
                  동네 범위 설정
              </Dropdown.Item>
            </Dropdown.Menu>
        </Dropdown>
        </Col>
      </Row>
      <Row className="justify-content-end">
        <Col md={3}>
          <Form.Select className="sortBy" onChange={handleSortByChange}>
            <option value="">정렬 방식</option>
            <option value="itemDesc">최신순</option>
            <option value="priceAsc">낮은 가격순</option>
            <option value="priceDesc">높은 가격순</option>
          </Form.Select>
          <form onSubmit={handleSearch}>
              <Row className="d-flex ">
                <Col sm={2}>
                   <input type="text" className="search-input" name="searchQuery" value={searchQueryInput} onChange={(e) => setSearchQueryInput(e.target.value)} />
                     <button type="submit">
                        <BiSearch />
                     </button>
                </Col>
              </Row>
           </form>
         </Col>
      </Row>

      <Row className="justify-content-end">
       <Col xs="auto">
                       <a href="/writeForm" className="ml-auto">
                           <Button className="writebutton">
                               <p className="writebuttontext">글쓰기</p>
                           </Button>
                       </a>
                     </Col>
        <Col md={3}>
         <Form onSubmit={searchByPrice} className="d-flex align-items-center m-filter">
           <Form.Group className="mr-2">
             <Form.Control
               type="number"
               name="minPrice"
               placeholder="최소 가격"
               value={minPriceInput}
               onChange={(e) => setMinPriceInput(e.target.value)}
             />
           </Form.Group>
           <Form.Group className="mr-2">
             <Form.Control
               type="number"
               name="maxPrice"
               placeholder="최대 가격"
               value={maxPriceInput}
               onChange={(e) => setMaxPriceInput(e.target.value)}
             />
           </Form.Group>
           <Button variant="primary" type="submit" className="mr-2 search" >
             검색
           </Button>
         </Form>
        </Col>
      </Row>
      <Row>
        <Col sm={1}>
          <Nav className="flex-column">
            <div>
              <Button href={`/boardList`} className="category-link">
                전체
              </Button>
            </div>
            {renderTopCategories}
          </Nav>
        </Col>
        <Col sm={1} className={selectedCategory ? 'show-subcategory' : 'hide-subcategory'}>
          {selectedCategory && <Nav className="flex-column">{renderSubCategories}</Nav>}
        </Col>

        <Col sm={9} className={selectedCategory ? 'pushed-content' : ''}>
          {tradeBoards ? (

            <InfiniteScroll
              dataLength={tradeBoards.length}
              next={fetchDataWithDelay}
              hasMore={hasMore}
              loader={
                <div className="loader-container">
                  <div className="loader"></div>
                </div>
              }

              scrollableTarget="scrollable-container"
            >
              <div className="sell-boards-container">
                {tradeBoards.map((board) => {
                  let timeAgo = formatDistanceToNow(parseISO(board.createdDate), { addSuffix: true, locale: ko });

                  return (
                    <div key={board.id} className="sell-board">
                      <Nav.Link href={`/boardDetail/${board.id}`}>
                        <div className="sell-board-image">
                          <img
                            src={`https://storage.cloud.google.com/reboot-minty-storage/${board.thumbnail}`}
                            alt={board.title}
                            className="sell-board-image"
                            effect="opacity"
                          />
                        </div>
                        <div className="sell-board-tt">{board.title}</div>
                        <div className="sell-board-p">{board.price.toLocaleString()} 원</div>
                        <Row>
                          <Col>
                            <div className="sell-board-nn">
                              <span>🤍 {board.interesting}</span>
                            </div>
                          </Col>
                          <Col>
                            <div className="sell-board-cd">
                              <span>{timeAgo}</span>
                            </div>
                          </Col>
                        </Row>
                        <div className="sell-board-ul">
                          <span> {board.sellArea}</span>
                        </div>
                      </Nav.Link>
                    </div>
                  );
                })}
              </div>
            </InfiniteScroll>
          ) : (
            <div className="no-results-message">게시물이 없습니다.</div>
          )}
        </Col>
      </Row>
        <Modal show={showUserLocationModal} onHide={handleUserLocationCloseModal} dialogClassName="custom-modal" bsClass="my-modal" >
            <Modal.Header closeButton>
                <Modal.Title>고객 위치 인증 리스트</Modal.Title>
            </Modal.Header>
            <Modal.Body>
             <div id="map" style={{ width: '100%', height: '100%' }}></div>
                <RangeSlider
                      value={mapLevel}
                      onChange={handleSliderChange}
                      step={50}
                      min={0}
                      max={100}
                      tooltip={false}
                    />

              <Row className="userLocationList">
                <ul className="list-group userLocation-list">
                  {userLocationList.map((result, index) => (
                    <li key={index} className="list-group-item">
                      <button
                        type="button"
                        className="btn btn-link address-link"
                        onClick={() => {
                          handleUserLocationCloseModal();
                        }}
                      >
                        {result.address}
                      </button>
                    </li>
                  ))}
                </ul>
              </Row>
            </Modal.Body>

            <Modal.Footer>
                <Button variant="secondary" onClick={handleUserLocationCloseModal}>
                    닫기
                </Button>
            </Modal.Footer>
        </Modal>

    </Container>
  );
}

export default BoardList;